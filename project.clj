(defproject org.iplantc/clj-icat-direct "0.0.2-SNAPSHOT"
  :description "A Clojure library for accessing the iRODS ICAT database directly."
  :url "http://github.com/iPlantCollaborativeOpenSource/clj-icat-direct/"
  :license {:name "BSD Standard License"
            :url "http://www.iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :scm {:connection "scm:git:git@github.com:iPlantCollaborativeOpenSource/kameleon.git"
        :developerConnection "scm:git:git@github.com:iPlantCollaborativeOpenSource/kameleon.git"
        :url "git@github.com:iPlantCollaborativeOpenSource/kameleon.git"}
  :pom-addition [:developers
                 [:developer
                  [:url "https://github.com/orgs/iPlantCollaborativeOpenSource/teams/iplant-devs"]]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [korma "0.3.0-RC5"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]]
  :repositories [["sonatype-nexus-snapshots"
                  {:url "https://oss.sonatype.org/content/repositories/snapshots"}]
                 ["sonatype-nexus-staging"
                  {:url "https://oss.sonatype.org/service/local/staging/deploy/maven2/"}]])
