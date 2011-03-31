package fr.xlim.ssd.opalgui.model.authenticate;

import fr.xlim.ssd.opal.library.params.CardConfig;
import fr.xlim.ssd.opal.library.params.CardConfigFactory;
import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author David Pequegnot, Julie Rispal
 */
public class AuthenticationModel {
    // Variable declaration
    private ArrayList<CardConfig> cardsConfig ;
    private ArrayList<String>     cardsName ;
    private int selectedCard ;
    private EventListenerList listeners ;

    // Singleton
    private static AuthenticationModel instance = null ;

    private AuthenticationModel() throws ParserConfigurationException,
            CardConfigNotFoundException {
        this.cardsConfig = new ArrayList<CardConfig>() ;
        this.cardsName   = new ArrayList<String>() ;
        this.listeners   = new EventListenerList() ;

        fillCardConfig();

        if ( this.cardsConfig.size() > 0 ) {
            selectedCard = 1 ;
        }
    }

    public synchronized static AuthenticationModel getInstance() throws ParserConfigurationException, CardConfigNotFoundException {
        if ( instance == null ) {
            instance = new AuthenticationModel() ;
        }
        return instance ;
    }

    public CardConfig getSelectedCard( ) throws CardConfigNotFoundException {
        if ( this.cardsConfig.size() == 0 ) {
            throw new CardConfigNotFoundException( "No cards found" ) ;
        }
        return ( cardsConfig.get( this.selectedCard ) ) ;
    }

    public String getSelectedCardName( ) throws CardConfigNotFoundException {
        if ( this.cardsConfig.size() == 0 ) {
            throw new CardConfigNotFoundException( "No cards found" ) ;
        }
        return ( this.cardsName.get( this.selectedCard ) ) ;
    }

    public int getSelectedCardIndex ( ) {
        return this.selectedCard ;
    }

    public ArrayList<CardConfig> getCardsConfig( ) {
        return this.cardsConfig ;
    }

    public ArrayList<String> getCardsName( ) {
        return this.cardsName ;
    }

    public void setSelectedCard( String cardName ) {
        boolean cardFound = false ;
        for ( int ind = 0 ; ind < this.cardsConfig.size() && !cardFound ; ++ind ) {
            if ( this.cardsName.get(ind).equals( cardName ) ) {
                cardFound    = true ;
                selectedCard = ind  ;
            }
        }
        fireSelectedProfilChanged() ;
    }

    public void addCardConfig( CardConfig cardConfig ) {
        // TODO : adding a card config
    }

    public void addProfilListener( AuthenticationProfilListener listener ) {
        this.listeners.add( AuthenticationProfilListener.class, listener ) ;
    }

    public void removeProfilListener( AuthenticationProfilListener listener ) {
        this.listeners.remove( AuthenticationProfilListener.class, listener ) ;
    }

    private void fireSelectedProfilChanged( ) {
        AuthenticationProfilListener [] listenerList =
                (AuthenticationProfilListener []) this.listeners.
                getListeners( AuthenticationProfilListener.class ) ;

        for ( AuthenticationProfilListener listener : listenerList ) {
            listener.profilChanged(
                    new AuthenticationProfilChangedEvent( this ) );
        }
    }
    
    private void fillCardConfig() throws ParserConfigurationException,
            CardConfigNotFoundException {
        try {

            InputStream input = CardConfigFactory.class.getResourceAsStream("/config.xml");

            Document document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(input);

            NodeList cardsList = document.getElementsByTagName("card");

            // looking for the card identifiant in config.xml file
            for (int i = 0; i < cardsList.getLength(); i++) {
                this.cardsName.add( ( ( Element ) cardsList.item( i ) ).
                        getAttribute( "name" ) ) ;
                this.cardsConfig.add( CardConfigFactory.
                        getCardConfig( ( ( Element ) cardsList.item( i ) ).
                        getAttribute( "name" ) ) ) ;
            }
        } catch (IOException e) {
            throw new CardConfigNotFoundException("cannot read the config.xml file");
        } catch (SAXException e) {
            throw new CardConfigNotFoundException("SAX error when reading config.xml file");
        } catch (ParserConfigurationException e) {
            throw new CardConfigNotFoundException("XML parsing error when reading config.xml file");
        }
    }
}
