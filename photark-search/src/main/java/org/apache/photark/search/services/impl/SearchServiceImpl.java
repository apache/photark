package org.apache.photark.search.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.photark.Image;
import org.apache.photark.ImageMetadata;
import org.apache.photark.search.services.SearchService;
import org.oasisopen.sca.annotation.AllowsPassByReference;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;

// TODO: review actions when index is corrupted
public class SearchServiceImpl implements SearchService {

    // TODO: a more dynamic way to specify field configuration should be
    // implemented
    // later
    final static public String IMAGE_NAME_FIELD = "name";
    
    final static public String IMAGE_ALBUM_FIELD = "album";

    final static public String IMAGE_DATE_POSTED_FIELD = "date_posted";

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

    private IndexSearcher indexSearcher;

    private QueryParser queryParser = new QueryParser("tag", this.defaultAnalyzer);
    {
        this.queryParser.setAllowLeadingWildcard(true);
    }

    @Property
    public String indexDirectoryPath;

    @Init
    public void init() throws IOException {
        FSDirectory dir = FSDirectory.getDirectory(new File(this.indexDirectoryPath));
        this.indexWriter = new IndexWriter(dir, this.defaultAnalyzer, MaxFieldLength.UNLIMITED);
        this.indexSearcher = new IndexSearcher(dir);

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
            TopDocs topDocs = this.indexSearcher.search(query, 100);

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
            this.indexWriter.updateDocument(idTerm, doc);
            
            this.indexWriter.commit();
            
        } catch (Exception e) {
            // create a new exception type for this
            throw new RuntimeException(e);
        }

    }

    private Document createDocument(Term id, String albumName, Image image) {
        Document doc = new Document();

        doc.add(new Field(id.field(), id.text(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field(IMAGE_ALBUM_FIELD, albumName, Store.YES, Index.ANALYZED));
        doc.add(new Field(IMAGE_NAME_FIELD, image.getName(), Store.YES, Index.ANALYZED));
        doc.add(new Field(IMAGE_DATE_POSTED_FIELD, DateTools.timeToString(image.getDatePosted().getTime(),
                                                                          Resolution.MINUTE), Store.YES,
                          Index.NOT_ANALYZED));

//        for (ImageMetadata metadata : image.getImageMetadata()) {
//
//            if (INDEXED_METADATA.contains(metadata.getKey())) {
//
//                // TODO: the field should be extracted from a configuration map
//                // instead of using the original metadata key
//                doc.add(new Field(metadata.getKey().replace(" ", "_"), metadata.getValue(), Store.YES,
//                                  Index.NOT_ANALYZED));
//
//            }
//
//        }

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

}
