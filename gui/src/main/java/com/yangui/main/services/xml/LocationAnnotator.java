package com.yangui.main.services.xml;

import java.util.Stack;

import javax.xml.stream.Location;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.w3c.dom.events.MutationEvent;


public class LocationAnnotator extends XMLFilterImpl {

    private Locator locator;
    private Stack<Element> elementStack = new Stack<Element>();
    private Stack<Locator> locatorStack = new Stack<Locator>();
    private UserDataHandler dataHandler = new LocationDataHandler();

    public LocationAnnotator(XMLReader xmlReader, Document dom) {
        super(xmlReader);

        EventListener modListener = new EventListener() {
            @Override
            public void handleEvent(Event e) {
                EventTarget target = ((MutationEvent) e).getTarget();
                elementStack.push((Element) target);
            }
        };
        ((EventTarget) dom).addEventListener("DOMNodeInserted", modListener, true);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        locatorStack.push(new LocatorImpl(locator));
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        super.endElement(uri, localName, qName);
      
        if (locatorStack.size() > 0) {
            Locator startLocator = locatorStack.pop();
          
            LocationData location = new LocationData(
                    startLocator.getSystemId(),
                    startLocator.getLineNumber(),
                    startLocator.getColumnNumber(),
                    locator.getLineNumber(),
                    locator.getColumnNumber());
            
            Element curElement = elementStack.pop();
            curElement.setUserData(LocationData.LOCATION_DATA_KEY, location, dataHandler);
        }
    }

    private class LocationDataHandler implements UserDataHandler {
        @Override
        public void handle(short operation, String key, Object data, Node src, Node dst) {
          
            if (src != null && dst != null) {
                Location locatonData = (Location)src.getUserData(LocationData.LOCATION_DATA_KEY);
                if (locatonData != null) {
                    dst.setUserData(LocationData.LOCATION_DATA_KEY, locatonData, dataHandler);
                }
            }
        }
    }
}