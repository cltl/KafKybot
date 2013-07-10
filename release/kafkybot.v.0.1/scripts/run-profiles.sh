java -Xmx812m -cp ../lib/KafKybot-1.0-jar-with-dependencies.jar vu.kafkybot.KafKybot --kaf-file "../example/bus-accident.ont.dep.kaf" --profiles "../profiles/profiles-sem.txt" --overview > "../example/bus-accident.ont.dep.kaf.sem.tpl"

java -Xmx812m -cp ../lib/KafKybot-1.0-jar-with-dependencies.jar vu.kafkybot.KafKybot --kaf-file "../example/bus-accident.ont.dep.kaf" --profiles "../profiles/profiles-syn.txt" --overview > "../example/bus-accident.ont.dep.kaf.syn.tpl"

java -Xmx812m -cp ../lib/KafKybot-1.0-jar-with-dependencies.jar vu.kafkybot.KafKybot --kaf-file "../example/bus-accident.ont.dep.kaf" --profiles "../profiles/profiles-pos.txt" --overview > "../example/bus-accident.ont.dep.kaf.pos.tpl"
