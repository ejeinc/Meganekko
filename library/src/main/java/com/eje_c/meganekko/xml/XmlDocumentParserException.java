package com.eje_c.meganekko.xml;

public class XmlDocumentParserException extends Exception {
    public XmlDocumentParserException() {
    }

    public XmlDocumentParserException(String detailMessage) {
        super(detailMessage);
    }

    public XmlDocumentParserException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public XmlDocumentParserException(Throwable throwable) {
        super(throwable);
    }
}
