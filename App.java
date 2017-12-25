import com.lexicalscope.jewel.cli.CliFactory;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args)
    {
        Command result = CliFactory.parseArguments(Command.class, args);
        int number = result.getNumber();
        String entityName = result.getEntity();
        boolean r = result.isReverse();
        boolean i = result.isIgnore();
        String fileName = args[args.length-1];
        String regexPattern = "";
        if (entityName.toLowerCase().equals("mention")){
            regexPattern = "(@\\w+)";
        } else if (entityName.toLowerCase().equals("hashtag") || entityName.toLowerCase().equals("hashtags")){
            regexPattern = "(#\\w+)";
        }
        Pattern p = Pattern.compile(regexPattern);
        Matcher m;
        try {
            String hashmen;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"utf-8"));
            String line;
            HashMap<String, Long> hashMap = new HashMap<>();
            List<Map.Entry<String, Long>> result1 = null;
            if(i){
                while ((line = reader.readLine()) != null) {
                    m = p.matcher(line);
                    while (m.find()) {
                        hashmen = m.group(0).toLowerCase();
                        if(hashMap.containsKey(hashmen))
                            hashMap.put(hashmen, hashMap.get(hashmen)+1L);
                        else
                            hashMap.put(hashmen, 1L);
                    }
                }
            }
            else{
                while ((line = reader.readLine()) != null) {
                    m = p.matcher(line);
                    while (m.find()) {
                        hashmen = m.group(0);
                        if(hashMap.containsKey(hashmen))
                            hashMap.put(hashmen, hashMap.get(hashmen)+1L);
                        else
                            hashMap.put(hashmen, 1L);
                    }
                }
            }
            reader.close();
            if (!r)
                result1 = hashMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(number)
                        .collect(Collectors.toList());
            else
                result1 = hashMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                        .limit(number)
                        .collect(Collectors.toList());
            if (result1 != null) {
                for (Map.Entry<String, Long> entry : result1) {
                    System.out.println(entry.getKey() + "\t" + entry.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
