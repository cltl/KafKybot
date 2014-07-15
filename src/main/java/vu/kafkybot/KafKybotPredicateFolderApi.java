package vu.kafkybot;

import eu.kyotoproject.kaf.KafSaxParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 9/10/13
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafKybotPredicateFolderApi {
    static public void main (String[] args) {
        HashMap<String, ArrayList<KafResult>> kafResultMap = new HashMap<String, ArrayList<KafResult>>();
       // String pathToKafFile = "//Tools/kafkybot.v.0.1/cars/Volkswagen-48-c6577c99c0be5cef4793cf54defb55e345fbe965.kaf.step.0.step.1.step.2.step.3.event.kaf";
        //String pathToKafFile = "/Tools/kafkybot.v.0.1/cars2";
        //String pathToProfiles = "/Tools/kafkybot.v.0.1/profiles/car-profiles-dep-all-event-subj-obj-en.txt";
        //String pathToKafFile = "/Projects/NEWSREADER/data/marit/kaf";
        String pathToKafFile = "/Users/piek/Desktop/Thomese/grillroom";
        String pathToProfiles = "/Tools/kafkybot.v.0.1/profiles/thomese-profiles.txt";
        String extension = ".kaf.event.kaf";
        String format = "NAF";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equals("--kaf-folder")) && args.length>i+1) {
                pathToKafFile = args[i+1];
            }
            else if ((arg.equals("--extension")) && args.length>i+1) {
                extension = args[i+1];
            }
            else if ((arg.equals("--profiles")) && args.length>i+1) {
                pathToProfiles = args[i+1];
            }
            else if ((arg.equals("--format")) && args.length>i+1) {
                format = args[i+1];
            }
        }

        File kafFile = new File(pathToKafFile);
        KafSaxParser kafSaxParser = new KafSaxParser();
        if (kafFile.isDirectory()) {
            ArrayList<String> files = Util.makeRecursiveFileListAll(pathToKafFile, extension);
            for (int f = 0; f < files.size(); f++) {
                File file = new File(files.get(f));
                if (!file.isDirectory()) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath()+".srl.kaf");
                        KafKybotPredicateStreamApi.processKafFile(kafSaxParser, file, pathToProfiles, format);
                        if (format.equalsIgnoreCase("kaf")) {
                            kafSaxParser.writeKafToStream(fos);
                        }
                        else if (format.equalsIgnoreCase("naf")) {
                            kafSaxParser.writeNafToStream(fos);
                        }
                        else if (format.equalsIgnoreCase("nafrdf")) {
                            kafSaxParser.writeNafToStream(fos);
                        }
                        fos.flush();
                        fos.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
    }

}
