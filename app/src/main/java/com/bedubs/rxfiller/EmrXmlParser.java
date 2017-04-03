package com.bedubs.rxfiller;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bedubs on 3/31/17.
 *
 */

class EmrXmlParser {
    private static final String ns = null;

    List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<PatientInfo> patients = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "emr");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("patient_info")) {
                patients.add(readPatientInfo(parser));
            } else {
                skip(parser);
            }
        }
        return patients;
    }

    private PatientInfo readPatientInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "patient_info");
        List<PatientOrders> orders = new ArrayList<>();
        String pName = "";
        String pId = parser.getAttributeValue(null, "id");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "name":
                    pName = readTag(parser, name);
                    break;
                case "patient_order":
                    orders.add(readPatientOrders(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new PatientInfo(pName, pId, orders);
    }

    private PatientOrders readPatientOrders(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "patient_order");
        String medicine = "";
        String dosage = "";
        String refillsRemaining = "";
        String lastRefill = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "medicine":
                    medicine = readTag(parser, name);
                    break;
                case "dosage":
                    dosage = readTag(parser, name);
                    break;
                case "refillsRemaining":
                    refillsRemaining = readTag(parser, name);
                    break;
                case "lastRefill":
                    lastRefill = readTag(parser, name);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new PatientOrders(medicine, dosage, refillsRemaining, lastRefill);
    }

    // Processes name tags in the feed.
    private String readTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return text;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
