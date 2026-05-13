# Empty stub. CFPropertyList 3.x does `require 'kconv'` at load time but
# never calls any Kconv method. Ruby 3.4 removed the kconv stdlib, so the
# bare require throws LoadError. Providing an empty file under the load
# path satisfies the require without bringing in NKF/Kconv semantics.
module Kconv; end
