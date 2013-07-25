package vu.kafkybot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/4/13
 * Time: 1:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileReader {

    //optional;type=participant;relation=agent;reference=*#human;pos=N*
    static public ArrayList<Profile> readProfiles (String file) {
        ArrayList<Profile> arrayList = new ArrayList<Profile>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            int nLines = 0;
            int profileLine = 0;
            Profile profile = null;
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                nLines++;
                if (inputLine.trim().length()>0) {
                    if (!inputLine.startsWith("#")) {
                        if (inputLine.startsWith("profile=")) {
                            if (profile!= null) {
                                if (profileCheck(profile, profileLine)) {
                                    arrayList.add(profile);
                                }
                            }
                            profileLine = nLines;
                            profile = new Profile();
                            String [] fields = inputLine.split(";");
                            for (int i = 0; i < fields.length; i++) {
                                String field = fields[i];
                                String [] pair = field.split("=");
                                if (pair.length==2) {
                                    String f = pair[0].trim();
                                    String v = pair[1].trim();
                                    if (f.equals("profile")) {
                                        profile.setName(v);
                                    }
                                    else if (f.equals("order")) {
                                        profile.setOrder(v);
                                    }
                                    else if (f.equals("confidence")) {
                                        try {
                                            int conf = Integer.parseInt(v);
                                            profile.setConfidence(conf);
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            String [] fields = inputLine.split(";");
                            TermProfile termProfile = new TermProfile();
                            for (int i = 0; i < fields.length; i++) {
                                String field = fields[i].trim();
                                String f = "";
                                String v = "";
                                String [] pair = field.split("=");
                                if (pair.length==1) {
                                   f = pair[0].trim();
                                }
                                else if (pair.length==2) {
                                    f = pair[0].trim();
                                    v = pair[1].trim();
                                }
                                if (!f.isEmpty()) {
                                    if (f.equals("type")) {
                                        termProfile.setType(v);
                                    }
                                    else if (f.equals("role")) {
                                        termProfile.setRole(v);
                                    }
                                    else if (f.equals("id")) {
                                        termProfile.setVariableId(v);
                                    }
                                    else if (f.equals("next")) {
                                        try {
                                            termProfile.setNext(Integer.parseInt(v));
                                        } catch (NumberFormatException e) {
                                          //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                    }
                                    else {
                                        if (f.startsWith("term:")) {
                                            f = f.substring(f.indexOf(":")+1);
                                            FeatureValuePair featureValuePair = new FeatureValuePair(f,v);
                                            termProfile.addFeatureValuePairToTermProperties(featureValuePair);
                                        }
                                        else if (f.startsWith("wf:")) {
                                            f = f.substring(f.indexOf(":")+1);
                                            FeatureValuePair featureValuePair = new FeatureValuePair(f,v);
                                            termProfile.addFeatureValuePairToTermTokens(featureValuePair);
                                        }
                                        else if (f.startsWith("chunk:")) {
                                            f = f.substring(f.indexOf(":")+1);
                                            FeatureValuePair featureValuePair = new FeatureValuePair(f,v);
                                            termProfile.addFeatureValuePairToTermChunks(featureValuePair);
                                        }
                                        else if (f.startsWith("dep:")) {
                                            f = f.substring(f.indexOf(":")+1);
                                            FeatureValuePair featureValuePair = new FeatureValuePair(f,v);
                                            termProfile.addFeatureValuePairToTermDependencies(featureValuePair);
                                        }
                                    }
                                }
                            }
                            if (profile!=null) {
                                profile.addTermProfiles(termProfile);
                            }
                        }
                    }
                }
            }
            if (profile!= null) {
                if (profileCheck(profile, profileLine)) {
                    arrayList.add(profile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return arrayList;
    }

    static boolean profileCheck (Profile profile, int profileLine) {
        boolean check = true;
        if(profile.getName().isEmpty()) {
            System.out.println("Empty name profile.toString() = " + profile.toString());
            check = false;
        }
        for (int i = 0; i < profile.getTermProfiles().size(); i++) {
            TermProfile termProfile = profile.getTermProfiles().get(i);
            if (termProfile.getType().isEmpty()) {
                System.out.println("profile.getName() = " + profile.getName());
                System.out.println("termProfile.getVariableId() = " + termProfile.getVariableId());
                System.out.println("Empty termProfile.getType() = " + termProfile.getType());
                check = false;
            }
            if (termProfile.getVariableId().isEmpty()) {
                System.out.println("profile.getName() = " + profile.getName());
                System.out.println("Empty termProfile.getVariableId() = " + termProfile.getVariableId());
                System.out.println("termProfile.getType() = " + termProfile.getType());
                check = false;
            }
        }
        if(!check) {
            System.out.println("SKIPPING THIS PROFILE AT LINE NR: "+profileLine);
        }
        return check;
    }
}
