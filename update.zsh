#!/bin/zsh
cd /Users/tejuo/Library/CloudStorage/OneDrive-Personal/Programming/IntelliJ/David-O-Meter || exit
git add "sentimentValues.json" "gitUpdates.log" "tweetsAnalysed.log"
git commit -m "update values"
git pull
git push
exit