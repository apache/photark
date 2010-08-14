/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.photark.search.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.photark.Image;
import org.apache.photark.search.services.ImageTags;
import org.apache.photark.search.services.SearchService;
import org.oasisopen.sca.annotation.AllowsPassByReference;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;

// TODO: review actions when index is corrupted
@Scope("COMPOSITE")
public class SearchServiceImpl implements SearchService {

    final static public String IMAGE_NAME_FIELD = "name";

    final static public String IMAGE_ALBUM_FIELD = "album";

    final static public String IMAGE_DATE_POSTED_FIELD = "date_posted";

    final static public String TAG_FIELD = "tag";

    final static private Set<String> INDEXED_METADATA = new HashSet<String>();

    static {
        Collections.addAll(INDEXED_METADATA,
                           "XResolution",
                           "YResolution",
                           "Date Time",
                           "Date Time Original",
                           "GPS Latitude Ref",
                           "GPS Latitude",
                           "GPS Longitude Ref",
                           "GPS Longitude");
    }

    final private Analyzer defaultAnalyzer = new TagAnalyzer();

    private IndexWriter indexWriter;

    private MultiFieldQueryParser queryParser =
        new MultiFieldQueryParser(new String[] {TAG_FIELD, IMAGE_NAME_FIELD, IMAGE_ALBUM_FIELD}, this.defaultAnalyzer);
    
    {
        this.queryParser.setAllowLeadingWildcard(true);
    }

    @Property
    public String indexDirectoryPath;

    private IndexSearcher indexSearcher;

    private Directory dir;

    @Init
    public void init() throws IOException {
        this.dir = FSDirectory.getDirectory(new File(this.indexDirectoryPath));
        this.indexWriter = new IndexWriter(dir, this.defaultAnalyzer, MaxFieldLength.UNLIMITED);
        this.indexSearcher = new IndexSearcher(this.dir);

        TermEnum terms = this.indexSearcher.getIndexReader().terms();
        System.out.println("termmmmmmmmmmmmmmmmmsssssss");
        while (terms.next()) {
            System.out.println(terms.term());
        }

    }

    public void clear() {

        try {
            this.indexWriter.deleteDocuments(new MatchAllDocsQuery());

        } catch (IOException e) {
            // create a new exception type for this
            throw new RuntimeException(e);
        }

    }

    public String[] search(String queryString) {

        try {
            Query query = this.queryParser.parse(queryString);
            TopDocs topDocs = this.indexSearcher.search(query, 10000);

            if (topDocs.scoreDocs.length > 0) {
                ArrayList<String> pictureNames = new ArrayList<String>(topDocs.scoreDocs.length);

                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document doc = this.indexSearcher.doc(scoreDoc.doc);
                    pictureNames.add(doc.get(IMAGE_ALBUM_FIELD) + '/' + doc.get(IMAGE_NAME_FIELD));
                }

                return pictureNames.toArray(new String[0]);

            }

        } catch (ParseException e) {
            throw new RuntimeException("could not parse query: " + queryString, e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new String[0];

    }

    @AllowsPassByReference
    public void imageAdded(String albumName, Image image) {

        try {
            Term idTerm = getImageIDTerm(albumName, image);
            Document doc = createDocument(idTerm, albumName, image);
            this.indexWriter.deleteDocuments(idTerm);
            this.indexWriter.addDocument(doc);

            this.indexWriter.commit();
            reopenIndexSearcher();

        } catch (Exception e) {
            // create a new exception type for this
            throw new RuntimeException(e);
        }

    }

    private void reopenIndexSearcher() throws CorruptIndexException, IOException {

        try {
            this.indexSearcher.close();
        } catch (IOException e) {
        }

        this.indexSearcher = new IndexSearcher(this.dir);

    }

    private Document createDocument(Term id, String albumName, Image image) {
        Document doc = new Document();

        doc.add(new Field(id.field(), id.text(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field(IMAGE_ALBUM_FIELD, albumName, Store.YES, Index.ANALYZED));
        doc.add(new Field(IMAGE_NAME_FIELD, image.getName(), Store.YES, Index.ANALYZED));
        doc.add(new Field(IMAGE_DATE_POSTED_FIELD, DateTools.timeToString(image.getDatePosted().getTime(),
                                                                          Resolution.MINUTE), Store.YES,
                          Index.NOT_ANALYZED));

        // for (ImageMetadata metadata : image.getImageMetadata()) {
        //
        // if (INDEXED_METADATA.contains(metadata.getKey())) {
        //
        // // TODO: the field should be extracted from a configuration map
        // // instead of using the original metadata key
        // doc.add(new Field(metadata.getKey().replace(" ", "_"),
        // metadata.getValue(), Store.YES,
        // Index.NOT_ANALYZED));
        //
        // }
        //
        // }

        return doc;

    }

    @AllowsPassByReference
    public void imageRemoved(String albumName, Image image) {
        try {
            this.indexWriter.deleteDocuments(getImageIDTerm(albumName, image));

        } catch (Exception e) {
            // TODO: create a new exception type for this
            throw new RuntimeException(e);
        }
    }

    @Destroy
    public void destroy() {

        try {
            this.indexSearcher.close();
        } catch (IOException e) {
        }

        try {
            this.indexWriter.close();
        } catch (Exception e) {
        }

    }

    private static Term getImageIDTerm(String albumName, Image image) {
        return new Term("id", albumName + '/' + image.getName());
    }

    private static Term getImageIDTerm(String albumName, String imageName) {
        return new Term("id", albumName + '/' + imageName);
    }

    public void addTag(String album, String imageName, String tag) {
        Term pictureID = getImageIDTerm(album, imageName);

        try {
            TopDocs docs = this.indexSearcher.search(new TermQuery(pictureID), 1);

            if (docs.scoreDocs.length < 1) {
                throw new IllegalArgumentException("picture not found: " + pictureID.text());
            }

            Document doc = this.indexSearcher.doc(docs.scoreDocs[0].doc);
            Field[] tagFields = doc.getFields(TAG_FIELD);

            for (Field tagField : tagFields) {
                
                if (tagField.stringValue().equals(tag)) {
                    return;
                }
                
            }
            
            doc.add(new Field(TAG_FIELD, tag, Store.YES, Index.ANALYZED));

            this.indexWriter.deleteDocuments(pictureID);
            this.indexWriter.addDocument(doc);
            this.indexWriter.commit();

            reopenIndexSearcher();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void removeTag(String album, String imageName, String tag) {
        Term pictureID = getImageIDTerm(album, imageName);

        try {
            TopDocs docs = this.indexSearcher.search(new TermQuery(pictureID), 1);

            if (docs.scoreDocs.length < 1) {
                throw new IllegalArgumentException("picture not found: " + pictureID.text());
            }

            Document doc = this.indexSearcher.doc(docs.scoreDocs[0].doc);
            List<?> fieldList = doc.getFields();
            Document newDoc = new Document();

            for (Object obj : fieldList) {
                Fieldable field = (Fieldable)obj;

                if (!field.name().equals(TAG_FIELD) || !field.stringValue().equals(tag)) {
                    newDoc.add(field);
                }

            }

            newDoc.setBoost(doc.getBoost());

            this.indexWriter.deleteDocuments(pictureID);
            this.indexWriter.addDocument(newDoc);
            this.indexWriter.commit();

            reopenIndexSearcher();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ImageTags getTags(String album, String imageName) {
        Term pictureID = getImageIDTerm(album, imageName);

        try {
            TopDocs topDocs = this.indexSearcher.search(new TermQuery(pictureID), 1);

            if (topDocs.scoreDocs.length > 0) {
                Document doc = this.indexSearcher.doc(topDocs.scoreDocs[0].doc);
                return new ImageTagsImpl(pictureID.text(), doc.getValues(TAG_FIELD));

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ImageTagsImpl(pictureID.text(), new String[0]);

    }
}
