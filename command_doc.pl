#!/usr/bin/perl

use strict;
use JSON::XS;

my $basedir = ".";
my @extension_files = glob("$basedir/extensions/*/src/main/resources/config.json"); # non deb installs
push @extension_files, glob("$basedir/scripts/*.json"); # cgi scripts

my @extension_templates;

foreach my $extension (@extension_files) {
	print "loading extension file $extension\n";
	my $res = open FILE, "<$extension";
	if (!$res) {
		print "extension file $extension not found!\n";
		next;
	}
	my @data = <FILE>;
	close FILE;
	my $json = (decode_json(join("", @data)));
	if (ref($json) eq 'ARRAY') {
		foreach my $element (@$json) {
			push @extension_templates, $element;
		}
	} else {
		push @extension_templates, $json;
	}

}

foreach my $extension (@extension_templates) {
	my $command_name = $extension->{'name'};
	my $hidden = $extension->{'hidden'};
	if ($hidden) {
		next;
	}
	if(defined $extension->{'description'}) {
	    print "* **$command_name** " . $extension->{'description'}."\n";
	    if (defined $extension->{'configuration'}) {
		if ($extension->{'immutable'}) {
	 	    next;
		}
		my $configuration = $extension->{'configuration'};
		foreach my $conf (keys %$configuration) {
			print "  - ".$conf." => ".$configuration->{$conf}."\n";
		}
	    }

	}
}
