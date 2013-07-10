package vu.kafkybot;

import eu.kyotoproject.kaf.GeoCountryObject;
import eu.kyotoproject.kaf.GeoPlaceObject;
import eu.kyotoproject.kaf.ISODate;
import eu.kyotoproject.kaf.KafSaxParser;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/5/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtractEntities {
    /*    public ArrayList<KafCoreferenceSet> kafCorefenceArrayList;
    public ArrayList<GeoCountryObject> kafCountryArrayList;
    public ArrayList<GeoPlaceObject> kafPlaceArrayList;
    public ArrayList<ISODate> kafDateArrayList;
    */
    /**
     *       <place countryCode="FR" countryName="France" fname="populated place" latitude="50.05" longitude="1.4166667" name="Eu" population="8425" timezone="Europe/Paris">
     <span id="t9419"/>
     </place>
     <country capital="Copenhagen" continent="EU" countryCode="DK" countryName="Denmark" east="15.1588354110718" fname="country" north="57.7484245300293" population="5484000" south="54.5623817443848" west="8.07560920715332">
     <span id="t221"/>
     </country>
     <dateInfo dateISO="2000" lemma="2000">
     <span id="t181"/>
     </dateInfo>
     */

    public String getDateAndPlace (KafSaxParser kafSaxParser, String termId) {
          ArrayList<GeoCountryObject> geoCountryObjects = new ArrayList<GeoCountryObject>();
          ArrayList<GeoPlaceObject> geoPlaceObjects = new ArrayList<GeoPlaceObject>();
          ArrayList<ISODate> isoDates = new ArrayList<ISODate>();
          geoCountryObjects = kafSaxParser.getCountriesForTermId(termId);
          if (geoCountryObjects.size()==0) {
              GeoCountryObject geoCountry = kafSaxParser.getMostFrequentGeoCountry();
              if (geoCountry!=null) {
                geoCountryObjects.add(geoCountry);
              }
          }
          geoPlaceObjects = kafSaxParser.getPlacesForTermId(termId);
          if (geoPlaceObjects.size()==0) {
              GeoPlaceObject geoPlace = kafSaxParser.getMostFrequentGeoPlace();
              if (geoPlace!=null) {
                  geoPlaceObjects.add(geoPlace);
              }
          }
          isoDates = kafSaxParser.getDatesForTermId(termId);
          if (isoDates.size()==0) {
              ISODate isoDate = kafSaxParser.getMostFrequentDate();
              if (isoDate!=null) {
                  isoDates.add(isoDate);
              }
          }
          String str = "";
          for (int i = 0; i < geoCountryObjects.size(); i++) {
              GeoCountryObject geoCountryObject = geoCountryObjects.get(i);
              /**
               <country capital="Copenhagen" continent="EU" countryCode="DK" countryName="Denmark" east="15.1588354110718" fname="country" north="57.7484245300293" population="5484000" south="54.5623817443848" west="8.07560920715332">
               <span id="t221"/>
               </country>
               */
                str += geoCountryObject.toEventFormat();
          }
          for (int i = 0; i < geoPlaceObjects.size(); i++) {
              GeoPlaceObject geoPlaceObject = geoPlaceObjects.get(i);
              /**
               <place countryCode="FR" countryName="France" fname="populated place" latitude="50.05" longitude="1.4166667" name="Eu" population="8425" timezone="Europe/Paris">
               <span id="t9419"/>
               </place>
               */
                str += geoPlaceObject.toEventFormat();
          }
          for (int i = 0; i < isoDates.size(); i++) {
              ISODate isoDate = isoDates.get(i);
              /**
               <dateInfo dateISO="2000" lemma="2000">
               <span id="t181"/>
               </dateInfo>
               */
                str += isoDate.toEventFormat();
          }
          return str;
    }


}
