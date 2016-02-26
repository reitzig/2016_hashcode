#!/usr/bin/ruby

# Copyright (c) 2016.
#
# This file is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This file is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this file. If not, see <http://www.gnu.org/licenses/>.

require 'fileutils'

SEP = ","
MSEP = " "
EOL = "\n"

if ARGV.size == 0
  puts "Usage: in2csv.rb [data.in]+"
  Process.exit
end

ARGV.each { |infile|
  lines = []
  File.open(infile, "r") { |f|
    lines = f.readlines.map { |line| line.split(/\s+/) }
  }
   
  world = {
    :rows       => lines[0][0].to_i,
    :columns    => lines[0][1].to_i,
    :deadline   => lines[0][3].to_i,
    :drones     => lines[0][2].to_i,
    :maxload    => lines[0][4].to_i, 
    :warehouses => lines[3][0].to_i,
    :products   => lines[1][0].to_i    
  }
  world[:orders] = lines[4 + world[:warehouses]*2][0].to_i
  
  folder = "data_analysis/" + infile.sub(/\.in$/, "")
  FileUtils::mkdir(folder) if !File.directory?(folder)
  
  # Basic data
  
  File.open("#{folder}/world.csv", "w") { |f|
    f.write(world.keys.map { |s| s.to_s }.join(SEP) + EOL)
    f.write(world.values.map { |i| i.to_s }.join(SEP) + EOL)
  }
    
  File.open("#{folder}/products.csv", "w") { |f|
    f.write(["id","weight"].join(SEP) + EOL)
    (0..world[:products]-1).each { |i|
      f.write([i.to_s, lines[2][i]].join(SEP) + EOL)
    }
  }
  
  File.open("#{folder}/warehouses.csv", "w") { |f|
    f.write(["id","x","y"].join(SEP) + EOL)
    (0..world[:warehouses]-1).each { |i|
      f.write([i.to_s, lines[4 + i*2][0], lines[4 + i*2][1]].join(SEP) + EOL)
    }
  }
  
  File.open("#{folder}/orders.csv", "w") { |f|
    f.write(["id","x","y"].join(SEP) + EOL)
    boff = 5 + 2*world[:warehouses]
    (0..world[:orders]-1).each { |i|
      f.write([i.to_s, lines[boff + i*3][0], lines[boff + i*3][1]].join(SEP) + EOL)
      
    }
  }
  
  # Matrix data
  
  File.open("#{folder}/orderXprod.csv", "w") { |f|
    boff = 5 + 2*world[:warehouses]
    (0..world[:orders]-1).each { |i|
      products = (0..world[:products]-1).map { |p| lines[boff + i*3 + 2].count(p.to_s) }
      f.write(products.join(MSEP) + EOL)
    }
  }
  
  File.open("#{folder}/whXprod.csv", "w") { |f|
    (0..world[:warehouses]-1).each { |i|
      f.write(lines[5 + i*2].join(MSEP) + EOL)
    }
  }
}

puts "CSV data written to data_analysis.\nRun `generateR.rb` in that folder to start your analysis."
