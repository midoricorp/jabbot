#!/usr/bin/perl

#requres json
#apt-get install libjson-perl libconfig-simple-perl

use strict;
use JSON::XS;
use Config::Simple;
use Scalar::Util qw(looks_like_number);


my @binding_templates = ();
my @extension_templates = ();

my $conf;
our $saved_values;

sub to_bool {
	my $val = shift;

	if ($val eq "true" || $val == 1) {
		return JSON::XS::true;
	} else {
		return JSON::XS::false;
	}
}

sub ask_multi {
	my $params = shift;
	my $question = $params->{'question'};
	my $values = $params->{'values'};
	my $save_key = $params->{'save_key'};


	my $save_value;
       	if (defined $save_key) {
		$save_value = $saved_values->param($save_key);
	}

	print "\n$question:\n";
	if (defined $values) {
		print "\nSpecify the number of each value you want separated by a space:\n";
		my $i = 0;

		foreach my $value (@$values) {
			print "\t$i - $value\n";
			$i++;
		}
	}
	print "\n";

	print ">";

	my $value;

	if (defined $save_value) {
		if (ref($save_value) eq 'ARRAY') {
			print join(" ", @$save_value) . "\n";
			$value=$save_value;
		} else {
			print "$save_value\n";
			$value=[$save_value];
		}
	} else {
		my $answer = <>;
		chomp($answer);

		my @answer_list = ();

		foreach my $ans (split(/ /, $answer)) {
			push @answer_list, $values->[$ans];
		}

		$value = \@answer_list;

	}

	return $value;
}

sub ask {
	my $params = shift;
	my $question = $params->{'question'};
	my $values = $params->{'values'};
	my $default = $params->{'default'};
	my $save_key = $params->{'save_key'};


	my $save_value;
       	if ($save_key) {
		$save_value = $saved_values->param($save_key);
	}

	print "\n$question:\n";
	if (defined $values) {
		print "\nPossible Values:\n";

		foreach my $value (@$values) {
			print "\t$value\n";
		}
	}
	print "\n";

	my $value = $default;

	if (defined $value) {
		print "[$value]>";
	} else {
		print ">";
	}

	if (defined $save_value) {
		print "$save_value\n";
		$value = $save_value;
	} else {
		my $answer = <>;
		chomp($answer);

		if ($answer ne "") {
			$value = $answer;
		}

		if ($save_key) {
			$saved_values->param($save_key, $value);
		}
	}

	# fix the type
	if (defined $default && JSON::XS::is_bool($default)) {
		$value = to_bool($value);
	} else {
		if (looks_like_number($default)) {
			$value = $value +0;
		} 
	}

	return $value;
}

sub makeBindings {
	my @bindings = ();
	foreach my $binding (@binding_templates) {
		my $b;
		$b->{'name'} = $binding->{'name'};
		$b->{'className'} = $binding->{'className'};
		push @bindings, $b;
	}
	$conf->{'bindings'} = \@bindings;
	
}


our @serverList = ();

sub makeRooms {
	my $type = shift;
	my $no_rooms= ask({question=>"Number of rooms for $type to join", save_key=>"binding.$type.no_rooms"});

	my @rooms;
	for (my $i = 1; $i <= $no_rooms; $i++) {
		my %room;
		$room{"name"} = ask({question=>"Name for room $i of $type", save_key=>"binding.$type.rooms.$i.name"});
		$room{"nickname"} = ask({question=>"Nickname to use in room $i of $type", save_key=>"binding.$type.rooms.$i.nickname"});
		push @rooms, \%room;
	}
	return \@rooms;
}

sub makeCommands {
	my $type = shift;
	my @commands;
	foreach my $extension (@extension_templates) {
		my $command_name = $extension->{'name'};
		my $use_command = ask({question=>"Add command '$command_name' to $type", values=>["Y", "N"], default=>"Y", save_key=>"binding.$type.command.$command_name.enable"});

		if (uc($use_command) eq "Y") {
			my %command;
			$command{"name"} = $command_name;
			$command{"className"} = $extension->{'className'};

			if (defined $extension->{'configuration'}) {
				if ($extension->{'immutable'}) {
					$command{"configuration"} = $extension->{'configuration'};
				} else {
					my $configuration = $extension->{'configuration'};
					foreach my $conf (keys $configuration) {
						$command{"configuration"}{$conf} = ask({question=>"value for '$conf' for $command_name command of $type", default=>$configuration->{$conf}, save_key=>"binding.$type.command.$command_name.$conf"});
					}
				}
			}
			push @commands, \%command;
		}
	}
	return \@commands;
}

sub makeServer {
	my @serverKeys = ('url', 'serverName', 'port', 'username', 'password', 'identifier', "commandPrefix", "debug");

	my $serverConfig = { 'debug' => JSON::XS::false, "commandPrefix"=>"!" };

	my $type = shift;

	$serverConfig->{'type'} = $type;

	my $binding;
	foreach $b (@binding_templates) {
		if ($b->{'name'} eq $type) {
			$binding = $b;
			last;
		}
	}

	if (! defined $binding) {
		print "\nBinding $type not found!\n";
		return;
	}

	foreach my $key (@serverKeys) {
		if (defined $binding->{$key}) {
			$serverConfig->{$key} = $binding->{$key};
		}	
	}

	foreach my $key (@serverKeys) {
		my $value = ask({question=>"$type value for $key", default=>$serverConfig->{$key}, save_key=>"binding.$type.$key"});
		$serverConfig->{$key} = $value;
	}
	my $parameters = $binding->{'parameters'};

	my $parm_map;

	if ($parameters) {
		foreach my $key (keys $parameters) {
			my $value = ask({question=>"$type value for $key", default=>$parameters->{$key}, save_key=>"binding.$type.$key"});
			$parm_map->{$key} = $value;
		}
		$serverConfig->{'parameters'} = $parm_map;
	}

	$serverConfig->{'rooms'} = makeRooms($type);
	$serverConfig->{'commands'} = makeCommands($type);
	push @serverList, $serverConfig;
}

sub makeServerList {
	my @binding_names = ();
	foreach my $binding (@binding_templates) {
		push @binding_names, $binding->{'name'};
	}

	my $types = ask_multi({question=>"Which Servers do you want to connect to?", values=>\@binding_names, save_key=>"server_list"});

	foreach my $binding (@$types) {
		makeServer($binding);
	}
	$conf->{'serverList'} = \@serverList;
}


# MAIN

my @binding_files = glob("bindings/*/src/main/resources/config.json");

foreach my $binding (@binding_files) {
	print "loading binding file $binding\n";
	open FILE, "<$binding";
	my @data = <FILE>;
	close FILE;
	push @binding_templates, decode_json(join("", @data));

}

my @extension_files = glob("extensions/*/src/main/resources/config.json");
push @extension_files, "jabbot-daemon/src/main/scripts/config.json";

foreach my $extension (@extension_files) {
	print "loading extension file $extension\n";
	open FILE, "<$extension";
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
#print JSON::XS->new->utf8(1)->pretty(1)->encode(\@extension_templates);


$saved_values = new Config::Simple(syntax=>'ini');
$saved_values->read('saved_values.ini');
makeBindings();
makeServerList();
$saved_values->save();

open FILE, ">jabbot.json";
print FILE JSON::XS->new->utf8(1)->pretty(1)->encode($conf);
close FILE;

print "jabbot.json witten, please move it to your config directory\n";
