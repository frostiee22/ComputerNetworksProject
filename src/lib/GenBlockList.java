package lib;

import java.io.*;
import java.util.ArrayList;


public class GenBlockList {

    public ArrayList<BlockIP> iplist = new ArrayList<>();

    public GenBlockList() throws IOException {
        // Open the file
        FileInputStream fstream;
        try {
            fstream = new FileInputStream("lib/block.txt");
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf);
            // path used by intellij IDEA
            fstream = new FileInputStream("src/lib/block.txt");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine = br.readLine();
        //Read File Line By Line
        while (strLine != null) {
            iplist.add(new BlockIP(strLine));
            strLine = br.readLine();
        }
        //Close the input stream
        br.close();
    }

    public boolean isblocked(String IP){
        for (int i =0;i<iplist.size();i++){
            if (IP.equalsIgnoreCase(iplist.get(i).getIP())){
                return true;
            }
        }
        return false;
    }


}
