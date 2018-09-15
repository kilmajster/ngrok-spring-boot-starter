# ngrok spring boot starter

### 👨‍🔛🌐️

This starter allows you to publish your locally running spring boot application to the Web! 👨‍🔛🌐️ So every time you want to
 share your application with your colleague, you'll be able to do it by sending him a link from the application logs!  
 
 Just like it was done in below screenshot:
 
![](https://raw.githubusercontent.com/createam-labs/ngrok-spring-boot-starter/development/ngrok-sample-screenshot.png
)[]()


### Configuration

##### ngrok configuration
If you haven't heard about ngrok yet, you can check out their site [here](https://ngrok.com/). How to create account, download, install & configure ngrok is described in their short [guide](https://dashboard.ngrok.com/get-started). Remember where you extracting binary file because this will be important in next step 👌

##### spring boot (auto)configuration

In your `application.properties` file add following (_this works on Windows 10_):

```properties

ngrok.path=C:\\dev\\ngrok\\ngrok.exe

```

So now, the starter will run ngrok http tunneling on specified `server.port`, which in spring boot, by default it will be `8080`.
