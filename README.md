# NetconfBrowser

JavaFX UI containing the following components:[Yang Schema 1.0](https://tools.ietf.org/html/rfc6020 "rfc6020"), Yang Query and [Netconf Protocol](https://tools.ietf.org/html/rfc6241 "Netconf Protocol").<br/>
This tool can connect to [Sysrepo](https://github.com/sysrepo/sysrepo) and [Opendaylight](https://www.opendaylight.org/).<br/>
For Opendaylight you must enable northbound Netconf by executing "feature:install odl-netconf-mdsal".

## Features

#### Schema
- [Schema Viewer](/resources/SchemaView1.png "SchemaViewer") with 2 modes of operation source or tree and support for enabling module features. 
- [Schema Editor](/resources/SchemaView2.png "SchemaEditor") with support for CRUD on Yang files. 
- [Schema Compiler](/resources/SchemaView3.png "SchemaCompiler") with error line number and error message reporting.

#### Network
- [Network Manager](/resources/DeviceView.png "Network Manager") with multiple Netconf connections.
- Matches UI capabilities with device capabilities.
- Supports both SSH password and SSH key authentication for each connection.
- Supports CRUD on device connections.		

#### Query
- [Query Manager](/resources/QueryView1.png "Query Manager") with multiple queries executed on Netconf connections.
- Query Wizard to automatically build RPC and Notification from Yang schema.
- Standard Netconf queries integrated in UI. 
- [Yang Validator](/resources/QueryView2.png "Yang Validator") for query XML with error line number and error message reporting.

## Install on Ubuntu/MacOS/Win From Tar (Only For Java 8)
```
apt-get install openjdk-8-jdk
apt-get install wget
apt-get install git-core
apt-get install maven

sudo curl -o NetconfBrowserApp-1.0.tar.gz -L https://raw.githubusercontent.com/bmdragan78/NetconfBrowser/master/resources/NetconfBrowserApp-1.0.tar.gz
tar -xf NetconfBrowserApp-1.0.tar.gz
cd NetconfBrowserApp/bin
sudo ./start.sh
or
sudo ./start.bat (win)
```
## Install Source 

```
cd ~
git clone https://github.com/bmdragan78/NetconfBrowser.git
cd NetconfBrowser
nano gui/src/main/java/resources/application.conf #update folder paths to     /home/myuser/NetconfBrowser/gui/linuxInstaller/NetconfBrowserApp/yangrepo/yang||template||logs
mvn install
cd gui/target/classes/
java com.yangui.gui.App
```

To generate tar archive from source

```
cd ~/NetconfBrowser/gui/linuxInstaller && sudo ./build_tar.sh
```

To create an Eclipse Project import "~/NetconfBrowser/pom.xml" as en existing Maven project.

## Demo

![Sysrepo Demo !](/resources/Sysrepo.png "Sysrepo Demo")

This demo uses NetconfBrowser UI, [Netopeer2 Server](https://github.com/CESNET/Netopeer2) and [Sysrepo Database](https://github.com/sysrepo/sysrepo) to implement one of the most common flows in Network Management:

1. User writes a configuration value on the device(using "edit-config" operation)
2. User reads the device state to see the effect of the configuration(using "get" operation)

Yang Module is [ietf-interfaces.yang](http://www.netconfcentral.org/modules/ietf-interfaces). 
A correlation is created between configuration leaf "/ietf-interfaces:interfaces/interface/enabled" and state leaf "/ietf-interfaces:interfaces-state/interface/oper-status"
by the [demoapplication/interfaces_manager.c](demoapplication/interfaces_manager.c "interfaces_manager.c")
This file could easily be modified to save interface configuration in "/etc/network/interfaces" and read 
interface statistics from "/proc/net/dev" in any Ubuntu/Debian OS.

To run demo:
 
1. Copy "~/NetconfBrowser/demoapplication/interfaces_manager.c" to "/home/netconf/sysrepo/sysrepo/examples" folder
2. Add the following lines in the "/home/netconf/sysrepo/sysrepo/examples/CMakeLists.txt" file
	```
	add_executable(interfaces_manager interfaces_manager.c)
	target_link_libraries(interfaces_manager sysrepo)
	```
3. Rebuild Sysrepo
	```
	cd sysrepo && mkdir build && cd build 
	cmake -DCMAKE_INSTALL_PREFIX=/usr -DCMAKE_BUILD_TYPE:String="Release" -DREPOSITORY_LOC:PATH=/etc/sysrepo ..
	make -j2 
	make install	
	```
4. Start "sysrepod" and "netopeer2-server" daemons
5. Run "sysrepo/build/examples/application_changes_example"
6. Run "sysrepo/build/examples/interfaces_manager"
7. Execute queries from NetconfBrowser UI (edit-config + get)
8. Observe how the state leaf reflects the values of config leaf for interfaces 'eth0' and 'eth1'

## Dependencies

- H2DB		 						&nbsp;&nbsp;&nbsp;&nbsp; https://www.h2database.com/html/main.html

- JFoenix							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://www.jfoenix.com/documentation.html

- RichTextFX 						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; https://github.com/FXMisc/RichTextFX

- AfterbunerFX						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; https://github.com/AdamBien/afterburner.fx

- FontawesomeFX 					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; https://github.com/Jerady/fontawesomefx-glyphsbrowser

- Juniper Netconf-Java  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; https://github.com/Juniper/netconf-java

- Opendaylight Yang Tools			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; https://docs.opendaylight.org/en/stable-fluorine/developer-guide/yang-tools.html

## License

[Apache License   Version 2.0   January 2004](http://www.apache.org/licenses/)

