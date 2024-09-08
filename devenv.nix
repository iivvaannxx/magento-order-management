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

    languages.java = {
      enable = true;
      maven.enable = true;

      # Java 17 is required.
      jdk.package = pkgs.jdk17;
    };

    # Node.js with TypeScript for the frontend.
    languages.typescript.enable = true;
    languages.javascript = {
      enable = true;
      pnpm.enable = true;
    };

    devenv.root = let
      devenvRootFileContent = builtins.readFile devenv-root.outPath;
    in
      pkgs.lib.mkIf (devenvRootFileContent != "") devenvRootFileContent;
  };
}
