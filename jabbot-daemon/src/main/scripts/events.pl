#!/usr/bin/perl

#Meetup events plugin
#requres json
#apt-get install libjson-perl libwww-perl liburi-encode-perl

use strict;
use LWP::UserAgent;
use URI::Encode qw(uri_encode uri_decode);
use JSON::XS;
use POSIX qw( strftime );

our $action = $ENV{'JABBOT_ACTION'};
our $command = $ENV{'JABBOT_COMMAND'};
our $from = $ENV{'JABBOT_FROM'};

if ($action eq "help") {
	print "Look Up meetups\n";
	print "Synax:\n";
	print "$command word\n";
} elsif ($action eq "run") {
	if (scalar(@ARGV) == 0 ) {
		print "What do you want to look up?\n";
	} elsif (scalar(@ARGV) > 0) {
		my $ua = LWP::UserAgent->new;
		my $key = 'your_meetup_key';
		my $server_endpoint ="http://api.meetup.com/2/open_events?&key=".uri_encode(join(' ', $key))."&text=" . uri_encode(join(' ', @ARGV));

		my $req = HTTP::Request->new(GET => $server_endpoint);
		$req->header('content-type' => 'application/json');

		my $resp = $ua->request($req);
		if ($resp->is_success) {
		    my $message = $resp->decoded_content;
		    my $json_data = decode_json ($message);
		    my $event_date_one = $json_data->{'results'}[0]->{'time'};
		    my $event_date_two = $json_data->{'results'}[1]->{'time'};
		    my $event_date_three = $json_data->{'results'}[2]->{'time'};
			my $date_one = strftime("%d-%m-%Y %H:%M:%S", localtime($event_date_one/1000));
			my $date_two = strftime("%d-%m-%Y %H:%M:%S", localtime($event_date_two/1000));
			my $date_three = strftime("%d-%m-%Y %H:%M:%S", localtime($event_date_three/1000));

		    	print "Here's a list of upcoming events for ". join(' ', @ARGV).": \n\n";
	            print "[". $json_data->{'results'}[0]->{'group'}->{'name'}."]: ";
	           	print $json_data->{'results'}[0]->{'name'}."\n";
	            print $json_data->{'results'}[0]->{'yes_rsvp_count'}." People Going\n";
	            print $json_data->{'results'}[0]->{'venue'}->{'name'}."\n";
	            print $json_data->{'results'}[0]->{'venue'}->{'address_1'}.", ";
	            print $json_data->{'results'}[0]->{'venue'}->{'city'}.", ";
	            print $json_data->{'results'}[0]->{'venue'}->{'country'}."\n";
	            print "--> ". $date_one." <-- \n";
	            print "---------------------------";
	            print "\n\n";
	            print "[". $json_data->{'results'}[1]->{'group'}->{'name'}."]: ";
	           	print $json_data->{'results'}[1]->{'name'}."\n";
	            print $json_data->{'results'}[1]->{'yes_rsvp_count'}." People Going\n";
	            print $json_data->{'results'}[1]->{'venue'}->{'name'}."\n";
	            print $json_data->{'results'}[1]->{'venue'}->{'address_1'}.", ";
	            print $json_data->{'results'}[1]->{'venue'}->{'city'}.", ";
	            print $json_data->{'results'}[1]->{'venue'}->{'country'}."\n";
	            print "--> ". $date_two." <-- \n";
	            print "---------------------------";
	            print "\n\n";
	            print "[". $json_data->{'results'}[2]->{'group'}->{'name'}."]: ";
	           	print $json_data->{'results'}[2]->{'name'}."\n";
	            print $json_data->{'results'}[2]->{'yes_rsvp_count'}." People Going\n";
	            print $json_data->{'results'}[2]->{'venue'}->{'name'}."\n";
	            print $json_data->{'results'}[2]->{'venue'}->{'address_1'}.", ";
	            print $json_data->{'results'}[2]->{'venue'}->{'city'}.", ";
	            print $json_data->{'results'}[2]->{'venue'}->{'country'}."\n";
	            print "--> ". $date_three." <-- \n";
	            print "---------------------------";
	            print "\n";

		}
		else {
			print "HTTP GET error code: ", $resp->code, "\n";
		        print "HTTP GET error message: ", $resp->message, "\n";
		        print "Crash and burn...";
		}
	}
}
