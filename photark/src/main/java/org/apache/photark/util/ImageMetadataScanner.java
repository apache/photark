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

package org.apache.photark.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.photark.ImageMetadata;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

public class ImageMetadataScanner {

    public static List<ImageMetadata> scanImageMetadata(String fileName, InputStream imageStream) {
        List<ImageMetadata> imageAttributes = new ArrayList<ImageMetadata>();
        try {
            
            // get all metadata stored in EXIF format (ie. from JPEG or TIFF). 
            IImageMetadata metadata = Sanselan.getMetadata(imageStream, fileName);
            if (metadata instanceof JpegImageMetadata) {
                    JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

                    // Jpeg EXIF metadata is stored in a TIFF-based directory structure
                    // and is identified with TIFF tags.
                    // Here we look for the "x resolution" tag, but
                    // we could just as easily search for any other tag.
                    // see the TiffConstants file for a list of TIFF tags.

                    // populate various interesting EXIF tags.
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.TIFF_TAG_XRESOLUTION);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.TIFF_TAG_YRESOLUTION);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.EXIF_TAG_CREATE_DATE);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.EXIF_TAG_ISO);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.EXIF_TAG_APERTURE_VALUE);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.EXIF_TAG_BRIGHTNESS_VALUE);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.GPS_TAG_GPS_LATITUDE_REF);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.GPS_TAG_GPS_LATITUDE);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.GPS_TAG_GPS_LONGITUDE_REF);
                    populateImageMetadata(imageAttributes, jpegMetadata, TiffConstants.GPS_TAG_GPS_LONGITUDE);

                    /*
                    // simple interface to GPS data
                    TiffImageMetadata exifMetadata = jpegMetadata.getExif();
                    if (null != exifMetadata)
                    {
                        TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                        if (null != gpsInfo)
                        {
                            String gpsDescription = gpsInfo.toString();
                            double longitude = gpsInfo.getLongitudeAsDegreesEast();
                            double latitude = gpsInfo.getLatitudeAsDegreesNorth();

                            System.out.println("    " + "GPS Description: " + gpsDescription);
                            System.out.println("    " + "GPS Longitude (Degrees East): " + longitude);
                            System.out.println("    " + "GPS Latitude (Degrees North): " + latitude);
                        }
                    }

                    // more specific example of how to manually access GPS values
                    TiffField gpsLatitudeRefField = jpegMetadata
                                    .findEXIFValueWithExactMatch(TiffConstants.GPS_TAG_GPS_LATITUDE_REF);
                    TiffField gpsLatitudeField = jpegMetadata
                                    .findEXIFValueWithExactMatch(TiffConstants.GPS_TAG_GPS_LATITUDE);
                    TiffField gpsLongitudeRefField = jpegMetadata
                                    .findEXIFValueWithExactMatch(TiffConstants.GPS_TAG_GPS_LONGITUDE_REF);
                    TiffField gpsLongitudeField = jpegMetadata
                                    .findEXIFValueWithExactMatch(TiffConstants.GPS_TAG_GPS_LONGITUDE);
                    if (gpsLatitudeRefField != null && gpsLatitudeField != null
                                    && gpsLongitudeRefField != null
                                    && gpsLongitudeField != null)
                    {
                            // all of these values are strings.
                            String gpsLatitudeRef = (String) gpsLatitudeRefField.getValue();
                            RationalNumber gpsLatitude[] = (RationalNumber[]) (gpsLatitudeField
                                            .getValue());
                            String gpsLongitudeRef = (String) gpsLongitudeRefField
                                            .getValue();
                            RationalNumber gpsLongitude[] = (RationalNumber[]) gpsLongitudeField
                                            .getValue();

                            RationalNumber gpsLatitudeDegrees = gpsLatitude[0];
                            RationalNumber gpsLatitudeMinutes = gpsLatitude[1];
                            RationalNumber gpsLatitudeSeconds = gpsLatitude[2];

                            RationalNumber gpsLongitudeDegrees = gpsLongitude[0];
                            RationalNumber gpsLongitudeMinutes = gpsLongitude[1];
                            RationalNumber gpsLongitudeSeconds = gpsLongitude[2];

                            // This will format the gps info like so:
                            //
                            // gpsLatitude: 8 degrees, 40 minutes, 42.2 seconds S
                            // gpsLongitude: 115 degrees, 26 minutes, 21.8 seconds E

                            System.out.println("    " + "GPS Latitude: "
                                            + gpsLatitudeDegrees.toDisplayString() + " degrees, "
                                            + gpsLatitudeMinutes.toDisplayString() + " minutes, "
                                            + gpsLatitudeSeconds.toDisplayString() + " seconds "
                                            + gpsLatitudeRef);
                            System.out.println("    " + "GPS Longitude: "
                                            + gpsLongitudeDegrees.toDisplayString() + " degrees, "
                                            + gpsLongitudeMinutes.toDisplayString() + " minutes, "
                                            + gpsLongitudeSeconds.toDisplayString() + " seconds "
                                            + gpsLongitudeRef);

                    }
                    

                    System.out.println();
                    */
                    ArrayList items = jpegMetadata.getItems();
                    for (int i = 0; i < items.size(); i++)
                    {

                        Object item = items.get(i);
                        if( item instanceof TiffImageMetadata.Item) {
                            TiffImageMetadata.Item tiffItem = (TiffImageMetadata.Item) item;
                            imageAttributes.add(new ImageMetadata(tiffItem.getKeyword(), tiffItem.getText()));
                        }
                        //System.out.println("    " + "item: " + item);
                        //IImageMetadataItem item = (IImageMetadataItem) items.get(i);
                        //imageAttributes.add(new ImageMetadata(item.))
                    }                    
            }

            //===========
            
            /*
            List<IImageMetadataItem> metadataItems = metadata.getItems();
            for(IImageMetadataItem metadataItem : metadataItems) {
                System.out.println(">>" + metadataItem);
            }
            */
        } catch (ImageReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageAttributes;
    }

    private static void printTagValue(JpegImageMetadata jpegMetadata, TagInfo tagInfo) throws ImageReadException, IOException {
        TiffField field = jpegMetadata.findEXIFValue(tagInfo);
        if (field == null)
            System.out.println(tagInfo.name + ": " + "Not Found.");
        else
            System.out.println(tagInfo.name + ": "
                               + field.getValueDescription());
    }
    
    /**
     * Check if an eFix attribute is available and ad that to the metadata attribute list
     * @param metadataAttributes
     * @param jpegMetadata
     * @param tagInfo
     * @throws ImageReadException
     * @throws IOException
     */
    private static void populateImageMetadata(List<ImageMetadata> metadataAttributes, JpegImageMetadata jpegMetadata, TagInfo tagInfo) throws ImageReadException, IOException {
        ImageMetadata metadata = getTagValue(jpegMetadata, tagInfo);
        if(metadata != null) {
            metadataAttributes.add(metadata);
        }
    }
    
    /**
     * Return Efix information wrapped into a ImageMetadata
     * @param jpegMetadata Collection of the efix metadatas
     * @param tagInfo the specific tagInfo being retrieved
     * @return the eFix information wrapped into a ImageMetadata
     * @throws ImageReadException
     * @throws IOException
     */
    private static ImageMetadata getTagValue(JpegImageMetadata jpegMetadata, TagInfo tagInfo) throws ImageReadException, IOException {
        ImageMetadata imageMetadata = null;
        TiffField field = jpegMetadata.findEXIFValue(tagInfo);
        if (field != null) {
            imageMetadata = new ImageMetadata(tagInfo.name, field.getValueDescription());
        }
        
        return imageMetadata;
    }

}
