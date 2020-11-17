Alfresco: Mime Type Blocker
===========================
Alfresco handles every file format, in fact there is no limitation out of the box in order to restrict allowed MIME types. Executables, databases and any other potentially harmful content can be stored inside.

This addon provides an Alfresco extension to define a mime type black list at ```alfresco-global.properties``` and one aspect ```Mime Type Restrictable``` to define which folders shall apply this restriction.

Once this addon is running, no uploading is allowed to selected folders by using black listed mime types.

The plugin is licensed under the [LGPL v3.0](http://www.gnu.org/licenses/lgpl-3.0.html). The current version is 3.0.0, which is compatible with Alfresco 6.x and onwards.

Downloading the ready-to-deploy-plugin
--------------------------------------
The binary distribution is made of two AMP files:

* [repo AMP](https://github.com/abhinavmishra14/alfresco-mimetype-blocker/releases/download/3.0.0/alfresco-mimetype-blocker-platform-1.0-SNAPSHOT.amp)
* [share AMP](https://github.com/abhinavmishra14/alfresco-mimetype-blocker/releases/download/3.0.0/alfresco-mimetype-blocker-share-1.0-SNAPSHOT.amp)

You can install it by using standard [Alfresco deployment tools](http://docs.alfresco.com/community/tasks/dev-extensions-tutorials-simple-module-install-amp.html)

Building the artifacts
----------------------
If you are new to Alfresco and the Alfresco Maven SDK, you should start by reading [Jeff Potts' tutorial on the subject](http://ecmarchitect.com/alfresco-developer-series-tutorials/maven-sdk/tutorial/tutorial.html).

You can build the artifacts from source code using maven
```$ mvn clean package```

Configuring the black list
--------------------------
Include and set the following properties at ```alfresco-global.properties``` on your Alfresco installation:
```sh
## Samples
## STARTS WITH video = video*
##   ENDS WITH xml   = *xml 
##    CONTAINS pdf   = *pdf*
##    EXACTLY ONE    = application/octet-stream
## MANY (use pipes)  = application/octet-stream|application/zip|video*
mimetypes.restricted.expression=video*
```

Above every video mime type will be blocked, but many other filters can be included.

Selecting restricted folders
----------------------------
By using default Alfresco Share folder action ```Manage Aspects```, *Mime Type Restrictable* aspect can be applied to desired folders.
