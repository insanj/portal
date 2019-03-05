SHELL:=/bin/bash
SOURCE_PATH=plugin
BUILD_PATH=build
EXTERNAL_PATH=external
SERVER_PATH=server
CRAFTBUKKIT_JAR_FILENAME=craftbukkit-1.13.2.jar
SPIGOT_JAR_FILENAME=spigot.jar
GIT_TAG:=$(shell git describe --tags)
OUTPUT_NAME=contractors
OUTPUT_VERSIONED_NAME=$(OUTPUT_NAME)-$(GIT_TAG)
JAR_DEPS_PATH=$(EXTERNAL_PATH)/$(SPIGOT_JAR_FILENAME):$(EXTERNAL_PATH)/$(CRAFTBUKKIT_JAR_FILENAME)

.PHONY: all
all: plugin server

.PHONY: plugin
plugin:
	# step 1 clean up / erase old version
	-rm -r -f $(BUILD_PATH)
	mkdir $(BUILD_PATH) && mkdir $(BUILD_PATH)/bin
	# step 2 compile the plugin into the bin dir
	javac -cp "$(JAR_DEPS_PATH)" -d $(BUILD_PATH)/bin $(SOURCE_PATH)/me/insanj/$(OUTPUT_NAME)/*.java
	#  jar tf external/craftbukkit-1.13.2.jar > hello.txt
	# step 3 copy config .yml to a new "build in progress" directory
	-cp -r $(SOURCE_PATH)/*.yml $(BUILD_PATH)/bin/	
	# step 3.5 copy all schematics to the build dir
	# -cp -r $(SOURCE_PATH)/me/insanj/$(OUTPUT_NAME)/*.schematic $(BUILD_PATH)/bin/me/insanj/$(OUTPUT_NAME)/
	# step 4 create JAR file using the "build in progress" folder
	jar -cvf $(BUILD_PATH)/$(OUTPUT_VERSIONED_NAME).jar -C $(BUILD_PATH)/bin .
	# step 5 remove any existing plugin on the server in the server folder
	-rm -r -f $(SERVER_PATH)/plugins/$(OUTPUT_VERSIONED_NAME).jar
	# step 6 copy the JAR file into the server to run it!
	rm -r -f $(SERVER_PATH)/plugins/$(OUTPUT_NAME)*.jar
	cp -r $(BUILD_PATH)/$(OUTPUT_VERSIONED_NAME).jar $(SERVER_PATH)/plugins/$(OUTPUT_VERSIONED_NAME).jar

.PHONY: server
server:
	# step 7 run the server!
	cd $(SERVER_PATH) && java -Xms1G -Xmx1G -jar -DIReallyKnowWhatIAmDoingISwear $(SPIGOT_JAR_FILENAME)

.PHONY: webapp
webapp:
	cd webapp && npm start