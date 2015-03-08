#!/usr/bin/perl

#urban dictionary plugin
#requres json
#apt-get install libjson-perl libwww-perl liburi-encode-perl

use strict;
use LWP::UserAgent;
use URI::Encode qw(uri_encode uri_decode);
use JSON::XS;

our $action = $ENV{'JABBOT_ACTION'};
our $command = $ENV{'JABBOT_COMMAND'};
our $from = $ENV{'JABBOT_FROM'};

sub filter
{
	my $message = shift();
	my @banlist = ("shit", "cum", "pussy", "dick", "vagina", "penis", "feces", "tits", "shat", "anus");

	foreach my $ban_word (@banlist) {
		if ($message =~ /$ban_word/iu) {
			return 1;
		}
	}
	return 0;
}

if ($action eq "help") {
	print "Look Up In UrbanDictionary\n";
	print "Synax:\n";
	print "$command word\n";
} elsif ($action eq "run") {
	if (scalar(@ARGV) == 0 ) {
		print "What do you want to look up?\n";
	} elsif (scalar(@ARGV) > 0) {
		my $ua = LWP::UserAgent->new;
		 

		my $server_endpoint = "http://api.urbandictionary.com/v0/define?term=" . uri_encode(join(' ', @ARGV));

		 
		my $req = HTTP::Request->new(GET => $server_endpoint);
		$req->header('content-type' => 'application/json');
		 
		my $resp = $ua->request($req);
		if ($resp->is_success) {
		    my $message = $resp->decoded_content;
		    my $json_data = decode_json ($message);
		    my $filtered_items = 0;
		    foreach my $node (@{$json_data->{'list'}}) {
			if(!filter($node->{'definition'})) {
				print $node->{'definition'}."\n";
				exit;
			}
			$filtered_items++;
		    }

		    if ($filtered_items > 0) {
			    print "$filtered_items censored definitions found: if you are feeling brave goto http://www.urbandictionary.com/define.php?term=".uri_encode(join(' ', @ARGV)); 
		    } else {
			    print "No defintion found for " . join(' ', @ARGV);
		    }
		}
		else {
			print "HTTP GET error code: ", $resp->code, "\n";
		        print "HTTP GET error message: ", $resp->message, "\n";
		}
	}
}
