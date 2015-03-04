#!/usr/bin/perl

use strict;
our $action = $ENV{'JABBOT_ACTION'};
our $command = $ENV{'JABBOT_COMMAND'};
our $from = $ENV{'JABBOT_FROM'};

if ($action eq "help") {
	print "Example Perl Command\n";
	print "Synax:\n";
	print "$command [target]\n";
} elsif ($action eq "run") {
	if (scalar(@ARGV) == 0 ) {
		print "Screw you! - $from\n";
	} elsif (scalar(@ARGV) > 0) {
		print "Fuck " . join(' ', @ARGV) . " -$from\n";
	}
}
