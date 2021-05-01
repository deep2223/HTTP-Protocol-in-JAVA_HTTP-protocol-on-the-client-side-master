
Compile the java file with below Command :  
	javac HttpClient.java

Run the file with below command : 
	java HttpClient

Test the code with following http request

General Usage:
httpc help

Get Usage:
httpc help get

Post Usage:
httpc help post

To Get with query parameters:
httpc get 'http://httpbin.org/get?course=networking&assignment=1'

To Get with verbose option:
httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'

To Post with inline data:
httpc post -h Content-Type:application/json -d '{"Assignment":1}' http://httpbin.org/post

To Support –o option:
httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' -o hello.txt

To support redirect:
httpc get https://airbrake.com/login/


<----  Created by : Deep Patel ------->


