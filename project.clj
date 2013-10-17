(defproject clj-icat-direct "0.0.1"
  :description "A Clojure library for accessing the iRODS ICAT database directly."
  :url "http://github.com/iPlantCollaborativeOpenSource/clj-icat-direct/"
  :license {:name "BSD"
            :url "http://github.com/iPlantCollaborativeOpenSource/clj-icat-direct/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [korma "0.3.0-RC5"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [slingshot "0.10.3"]])
