import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
/* This is HttpClient class for HTTP client application.
 * <ul>
 * <li>Build HTTP client library.</li>
 * <li>Program HTTP client application (curl command).</li>
 * <li>Implement more HTTP protocol specifications.</li>
 * <li>Enhance the functionalities of the HTTP client. </li>
 * </ul>
 * @author Jemish
 * @author Deep
 */
public class HttpClient {
public static void main(String[] args) throws UnknownHostException, IOException, EOFException {
    boolean redirection=false;
    while(true){
        String clientRequest;
        if(!redirection){
          System.out.print(">");
          Scanner scanner=new Scanner(System.in);
          clientRequest = scanner.nextLine();
          if(clientRequest.isEmpty() || clientRequest.length()==0){
              System.out.println("Invalid Command");
              continue;
          } 
        }
        else{
          System.out.println("\nRedirection Successful\n");
          clientRequest = "httpc get -v http://httpbin.org/get?";
        }
        
    String[] clientRequestArray = clientRequest.split(" ");
    ArrayList<String> clientRequestList = new ArrayList<>();
    
    for (int i = 0; i < clientRequestArray.length; i++) {
        clientRequestList.add(clientRequestArray[i]);
    }
    
    //Help command imformation
    if(clientRequestList.contains("help")){
            if(clientRequestList.contains("get")){
                System.out.println("usage: httpc get [-v] [-h key:value] URL\nGet executes a HTTP GET request for "
                    + "a given URL.\n\n  -v Prints the detail of the response such as protocol, status, and headers."
                        + "\n  -h key:value Associates headers to HTTP Request with the format 'key:value'.");
            }
            else if(clientRequestList.contains("post")){
                System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\nPost executes a HTTP "
                        + "POST request for a given URL with inline data or from file.\n  -v Prints the detail of the response "
                        + "such as protocol, status, and headers.\n  -h key:value Associates headers to HTTP Request with the "
                        + "format 'key:value'.\n  -d string Associates an inline data to the body HTTP POST request."
                        + "\n  -f file Associates the content of a file to the body HTTP POST request.\n\nEither [-d] or [-f] "
                        + "can be used but not both.");
            }
            else{
                System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n"
                        + "Usage:\n  httpc command [arguments]\nThe commands are:\n  get  executes a HTTP GET request and prints the response.\n"
                        + "  post executes a HTTP POST request and prints the response.\n  help prints this screen.\n\nUse \"httpc help [command]\" "
                        + "for more information about a command.");
            }
    }
    else{
     
        // -d and -f can not be used at same time
        if(clientRequestList.contains("-d") && clientRequestList.contains("-f")){
            System.out.println("Either [-d] or [-f] can be used but not both.");
            continue;
        }
        
    // to get position of the URL
    int urlPosition=1;
    if(clientRequestList.contains("-o")){
        urlPosition=3;
    }
    //URL of Web server
    String url =  clientRequestList.get(clientRequestList.size()-urlPosition).substring(0, clientRequestList.get(clientRequestList.size()-urlPosition).lastIndexOf('/')+1);
   // System.out.print(url);
    
    if(url.contains("\'")){
        url=url.replace("\'", "");
    }
    
    // getting host from URL
    String host = new URL(url).getHost();
    
    //Open connection 
    Socket client = new Socket(host, 80);
    OutputStream outputStream = client.getOutputStream();
    
    // getting request method like get, post etc. 
    String httpMethod=clientRequestList.get(1).toUpperCase();
    
    //getting parameters(text after host)
    String queryParameters=clientRequestList.get(clientRequestList.size()-urlPosition).substring(clientRequestList.get(clientRequestList.size()-urlPosition).lastIndexOf('/'));
   
    if(queryParameters.contains("\'")){
        queryParameters=queryParameters.replace("\'", "");
    }
    
    PrintWriter printWriter = new PrintWriter(outputStream);
 
    // Preparing request by adding method and parameters
    printWriter.print(httpMethod+" "+queryParameters+" HTTP/1.1\r\n");
    
    // Adding host to request
    printWriter.print("Host: "+host+"\r\n");
    
    String inlineData=new String();
    StringBuffer fileData=new StringBuffer();
    
    // for the inline data
    if(clientRequestList.contains("-d")) {
        inlineData=clientRequestList.get(clientRequestList.indexOf("-d")+1);
        if(inlineData.contains("\'")){
        inlineData=inlineData.replace("\'", "");
        }
        printWriter.print("Content-Length: "+inlineData.length()+"\r\n");
    }
    //for the file data
    else if(clientRequestList.contains("-f")){
        File file=new File(clientRequestList.get(clientRequestList.indexOf("-f")+1));
        BufferedReader br = new BufferedReader(new FileReader(file)); 
        String st;
        while ((st = br.readLine()) != null){
            fileData.append(st);
        }
        printWriter.println("Content-Length: "+fileData.length()+"\r\n");
    }
    
    // Code for header manipulation starts, if request has -h command (adding header information to the request)
    if(clientRequestList.contains("-h")){
        if(!clientRequestList.contains("-d") && !clientRequestList.contains("-f")){
            int noOfHeaders=clientRequestList.size()-1-clientRequestList.indexOf("-h")-1;
            for (int i = 1; i <= noOfHeaders; i++) {
                printWriter.print(clientRequestList.get(clientRequestList.indexOf("-h")+i)+"\r\n");
                }
        }
        else if(clientRequestList.contains("-d") || clientRequestList.contains("-f")){
            int noOfHeaders=0;
            if(clientRequestList.contains("-d")){
              noOfHeaders=clientRequestList.indexOf("-d")-clientRequestList.indexOf("-h")-1;
            }
            else if(clientRequestList.contains("-f")){
              noOfHeaders=clientRequestList.indexOf("-f")-clientRequestList.indexOf("-h")-1;
            }
            for (int i = 1; i <= noOfHeaders; i++) {
              printWriter.print(clientRequestList.get(clientRequestList.indexOf("-h")+i)+"\r\n");
            }
        }
    } 
    
    // Code for adding in-line data and file data to the request
    
    if(clientRequestList.contains("-d")){
        printWriter.print("\r\n");
        printWriter.print(inlineData);
    }else if(clientRequestList.contains("-f")){
        printWriter.print(fileData);
        printWriter.print("\r\n");
    }else{
        printWriter.print("\r\n");
    }
    printWriter.flush();
    
    
    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
    String temp;
    // if request contains 'verbose'(-v) command
   
    String statusCode = br.readLine();
   
    //if status code contain 3**";
    String[] strArr=statusCode.split(" ");
    if(strArr[1].contains("3")){
        // if redirect code in status
        System.out.println(statusCode);
        redirection=true;
        continue;
    }
    redirection=false;
    
    // printing response to the mentioned file 
    if(clientRequestList.contains("-o")){
        String filePath=clientRequestList.get(clientRequestList.size()-1);
        
        FileWriter file=new FileWriter(filePath,true);
        BufferedWriter bufferWriter=new BufferedWriter(file);
        PrintWriter printWriter1=new PrintWriter(bufferWriter);
        
     // if request does contain 'verbose'(-v) command
        if(clientRequestList.contains("-v")){
            printWriter1.println(statusCode);
        while((temp = br.readLine()) !=null ) {
            printWriter1.println(temp);
            if(temp.equals("}"))
                break;
            }
        }
        // if request does not contain 'verbose'(-v) command
        else{
            int flag=0;
            while((temp = br.readLine()) !=null ) {
                if(temp.trim().equals("{")) flag=1;
                if(flag==1){
                    printWriter1.println(temp);
                    if(temp.equals("}"))
                        break;
                }
            }
        }
        printWriter1.flush();
        printWriter1.close();
    }
    // Printing response to the console
    else{
    if(clientRequestList.contains("-v")){
        System.out.println(statusCode);
    while((temp = br.readLine()) !=null ) {
        System.out.println(temp);
        if(temp.equals("}"))
            break;
        }
    }
    // if request does not contain 'verbose'(-v) command
    else{
        int flag=0;
        while((temp = br.readLine()) !=null) {
            if(temp.trim().equals("{")) flag=1;
            if(flag==1){
            System.out.println(temp);
            if(temp.equals("}"))
                break;
            }
        }
        }
    }
    br.close();
    client.close();
    }
    }
}

}
