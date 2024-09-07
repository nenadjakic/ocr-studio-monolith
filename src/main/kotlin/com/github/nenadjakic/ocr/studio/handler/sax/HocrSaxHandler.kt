package com.github.nenadjakic.ocr.studio.handler.sax

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class HocrSaxHandler: DefaultHandler() {
    enum class InsideElement { HEAD, BODY, OTHER}
    private var insideElement = InsideElement.OTHER

    private lateinit var bodyBuilder: StringBuilder
    private lateinit var headBuilder: StringBuilder

    val body: String
        get() = bodyBuilder.toString()

    val head: String
        get() = headBuilder.toString()


    override fun startDocument() {
        bodyBuilder = StringBuilder()
        headBuilder = StringBuilder()
    }

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        if (qName.equals("head", true)) {
            insideElement = InsideElement.HEAD
        } else if (qName.equals("body", true)) {
            insideElement = InsideElement.BODY
        } else if (insideElement == InsideElement.BODY) {
            appendStartElement(bodyBuilder, qName, attributes)
        } else if (insideElement == InsideElement.HEAD) {
            appendStartElement(headBuilder, qName, attributes)
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        if (qName.equals("body", true) || qName.equals("head", true)) {
            insideElement = InsideElement.OTHER
        } else if (insideElement == InsideElement.BODY) {
            bodyBuilder.append("</$qName>")
        } else if (insideElement == InsideElement.HEAD) {
            headBuilder.append("</$qName>")
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        if (insideElement == InsideElement.BODY) {
            bodyBuilder.append(ch, start, length)
        } else if (insideElement == InsideElement.HEAD) {
            headBuilder.append(ch, start, length)
        }
    }

    private fun appendStartElement(stringBuilder: StringBuilder, qName: String?, attributes: Attributes?) {
        stringBuilder.append("<$qName")

        if (attributes != null && attributes.length > 0) {
            for (i in 0 until attributes.length) {
                val attributeName = attributes.getQName(i)
                val attributeValue = attributes.getValue(i)
                if (attributeValue.contains("'")) {
                    stringBuilder.append(" $attributeName=\"$attributeValue\"")
                } else {
                    stringBuilder.append(" $attributeName='$attributeValue'")
                }
            }
        }
        stringBuilder.append(">")
    }
}