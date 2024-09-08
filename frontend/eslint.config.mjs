import antfu from "@antfu/eslint-config";
import pluginQuery from "@tanstack/eslint-plugin-query";

export default antfu(
  {
    formatters: {
      css: "prettier",
      html: "prettier",
    },

    react: true,
    lessOpinionated: true,

    stylistic: {
      quotes: "double",
      indent: 2,
      semi: true,
    },
  },

  ...pluginQuery.configs["flat/recommended"],
);
