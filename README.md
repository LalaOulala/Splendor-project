# Splendor-project

## Pense-bête Git (setup)

* J’ai initialisé le dépôt local (`git init`) puis fait un premier commit (README).
* J’ai configuré le remote en **SSH** (`git@github.com:...`) car j’avais des erreurs en HTTPS du type *RPC failed / HTTP 400* lors du `git push`.
* J’ai ajouté un `.gitignore` pour ne **pas versionner** les fichiers générés :

  * `*.class` (fichiers compilés Java)
  * `*.ctxt` (fichiers BlueJ)
  * `.DS_Store`, `*.log`
* Je push uniquement le **code source** (`*.java`) + les **ressources utiles** (ex: `stats.csv`, `package.bluej`, etc.).

Commandes utiles :

* `git status` (voir ce qui va être commit)
* `git add …` puis `git commit -m "..."` puis `git push`
