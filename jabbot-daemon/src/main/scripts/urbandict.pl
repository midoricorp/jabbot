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

print "action=$action\n";

if ($action eq "help") {
	print "Look Up In UrbanDictionary\n";
	print "Synax:\n";
	print "$command word\n";
} elsif ($action eq "run") {
	if (scalar(@ARGV) == 0 ) {
		print "Screw you! - $from\n";
	} elsif (scalar(@ARGV) > 0) {
		my $ua = LWP::UserAgent->new;
		 

		my $server_endpoint = "http://api.urbandictionary.com/v0/define?term=" . uri_encode(join(' ', @ARGV));
		print "fetching $server_endpoint\n";

		 
		my $req = HTTP::Request->new(GET => $server_endpoint);
		$req->header('content-type' => 'application/json');
		 
		my $resp = $ua->request($req);
		if ($resp->is_success) {
		    my $message = $resp->decoded_content;
		    my $json_data = decode_json ($message);
	            print $json_data->{'list'}[0]->{'definition'}."\n";
		}
		else {
			print "HTTP GET error code: ", $resp->code, "\n";
		        print "HTTP GET error message: ", $resp->message, "\n";
		}
	}
}
