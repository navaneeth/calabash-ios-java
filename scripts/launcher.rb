
require 'calabash-cucumber/launcher'

@calabash_launcher = Calabash::Cucumber::Launcher.new
unless @calabash_launcher.calabash_no_launch?
	@calabash_launcher.relaunch
    @calabash_launcher.calabash_notify(self)
end
