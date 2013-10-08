Calabash-ios-java developer guide
==================================

Calabash-ios-java uses `JRuby` to invoke the Ruby client maintained by calabash-ios developers. calabash-ios gem and all other dependent gems are zipped and put it to the distributable JAR file of `calabash-ios-java`.  While executing, `calabash-ios-java` will extract the gems from the JAR and sets up the gem path for the JRuby runtime.

To create a distibutable package, follow the below instructions. 

Create a temporary directory to get all the gems

```shell
rvm gemset create calabash-cucumber-latest-gems
rvm use ruby-1.9.3-p194@calabash-cucumber-latest-gems
```

Install the calabash-cucumber gem. This will install the gem and it's dependencies to the temporary gemset created before.

```shell
gem install calabash-cucumber --no-ri --no-rdoc
```

Download latest JRuby from `http://jruby.org.s3.amazonaws.com/downloads/<version>/jruby-bin-<version>.tar.gz`. Unzip and move this directory to the gemset directory as `jruby.home`. 

```shell
cd ~/.rvm/gems/ruby-1.9.3-p194@calabash-cucumber-latest-gems
wget http://jruby.org.s3.amazonaws.com/downloads/1.7.5/jruby-bin-1.7.5.tar.gz
tar -zxvf jruby-bin-1.7.5.tar.gz
mv jruby-1.7.5  jruby.home
rm jruby-bin-1.7.5.tar.gz
```

Delete unwanted files from the `jruby.home` directory. Create a zip of all the contents.

```shell
zip -r gems.zip .
```

Move the `gems.zip` file to `calabash-ios-java` directory and make the distro.

```shell
ant -Dgems.zip.path=gems.zip distro 
```

This will make the distributable files inside the `build` directory. Grab the JAR from the distribution, test it and release!