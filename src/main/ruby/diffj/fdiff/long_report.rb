#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/fdiff/report'
require 'diffj/io/diffwriter/writer'

include Java

module DiffJ
  module Analysis
    # Reports differences in long form.
    class LongReport < Report
      include Loggable
      
      def initialize writer, show_context, highlight
        super writer

        @dwcls = show_context ? (highlight ?  DiffJ::IO::Diff::ContextHighlightWriter : DiffJ::IO::Diff::ContextWriter) : DiffJ::IO::Diff::NoContextWriter
        @from_contents = nil
        @to_contents = nil
      end

      def write_differences
        begin
          from_lines = @from_contents.split "\n"
          to_lines = @to_contents.split "\n"
          dw = @dwcls.new from_lines, to_lines
          
          differences.each do |fdiff|
            str = dw.difference fdiff
            @writer.write(str)
          end

          @writer.flush
          # we can't close STDOUT:
          # writer.close
        rescue java.io.IOException => ioe
          # nothing
        end
      end

      def reset from_file_name, from_contents, to_file_name, to_contents
        @from_contents = from_contents
        @to_contents = to_contents
        super
      end
    end
  end
end