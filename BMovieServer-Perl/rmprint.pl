#!/usr/bin/perl
while(<>) {
  s/print[^;]*;//g;
  print $_;
}
