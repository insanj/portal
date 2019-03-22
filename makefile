SHELL:=/bin/bash
JAVA_HOME:=/usr/lib/jvm/jdk1.8.0_131

JAVAC_CMD=/usr/lib/jvm/jdk1.8.0_131/bin/javac
JAR_CMD=/usr/lib/jvm/jdk1.8.0_131/bin/jar

FIND_JAVA_FILES:=$(shell find . -name '*.java')
GIT_TAG:=$(shell git describe --tags)

OUTPUT_NAME=portal
SOURCE_PATH=plugin
BUILD_PATH=build
EXTERNAL_PATH=external
SERVER_PATH=server

CRAFTBUKKIT_JAR_FILENAME=craftbukkit-1.13.2.jar
JAR_DEPS_PATH=$(EXTERNAL_PATH)/$(CRAFTBUKKIT_JAR_FILENAME)
OUTPUT_VERSIONED_NAME=$(OUTPUT_NAME)-$(GIT_TAG)

.PHONY: all
all: plugin server

.PHONY: plugin
plugin:
	export JAVA_HOME=$(JAVA_HOME)
	# step 1 clean up / erase old version
	-rm -r -f $(BUILD_PATH)
	mkdir $(BUILD_PATH) && mkdir $(BUILD_PATH)/bin
	# step 2 compile the plugin into the bin dir
	$(JAVAC_CMD) -cp "$(JAR_DEPS_PATH)" -d $(BUILD_PATH)/bin $(FIND_JAVA_FILES)
	# step 3 copy config .yml to a new "build in progress" directory
	-cp -r $(SOURCE_PATH)/*.yml $(BUILD_PATH)/bin/
	# step 4 create JAR file using the "build in progress" folder
	$(JAR_CMD) -cvf $(BUILD_PATH)/$(OUTPUT_VERSIONED_NAME).jar -C $(BUILD_PATH)/bin .
	#  javap -verbose build/bin/me/insanj/portal/Portal.class

.PHONY: clean
clean:
	# step 5 remove any existing plugin on the server in the server folder
	-rm -r -f $(SERVER_PATH)
	mkdir $(SERVER_PATH) && mkdir $(SERVER_PATH)/plugins
	echo "eula=true" > $(SERVER_PATH)/eula.txt
	cp -R $(EXTERNAL_PATH)/$(CRAFTBUKKIT_JAR_FILENAME) $(SERVER_PATH)/$(CRAFTBUKKIT_JAR_FILENAME)

.PHONY: server
server:
	# step 6 copy the JAR file into the server to run it!
	-rm -r $(SERVER_PATH)/plugins/$(OUTPUT_NAME)*.jar
	cp -R $(BUILD_PATH)/$(OUTPUT_VERSIONED_NAME).jar $(SERVER_PATH)/plugins/
	cd $(SERVER_PATH) && java -Xms1G -Xmx1G -jar -DIReallyKnowWhatIAmDoingISwear $(CRAFTBUKKIT_JAR_FILENAME)
