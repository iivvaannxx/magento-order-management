{
  config,
  pkgs,
  devenv-root,
  ...
} @ args:{

  shells.default = {
    name = "Adobe Junior Software Engineer Test";
    packages = with pkgs; [
      git
      curl
    ];

    env = {
      MAVEN_OPTS = "-Xmx1024m";
    };
    
    languages.java = {
      enable = true;
      maven.enable = true;

      # Java 17 is required.
      jdk.package = pkgs.jdk17;
    };

    devenv.root = let
      devenvRootFileContent = builtins.readFile devenv-root.outPath;
    in
      pkgs.lib.mkIf (devenvRootFileContent != "") devenvRootFileContent;
  };
}
