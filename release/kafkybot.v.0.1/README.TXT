Project: vu.kafkybot.KafKybot
Version: 1.0
Copyright: VU University Amsterdam, Piek Vossen
Email: piek.vossen@vu.nl

FILE LISTING:
	COPYING-GPL.TXT
	LICENSESOFTWARE.TXT
	README.TXT
	examples
		bus-accident.ont.dep.kaf
		bus-accident.ont.dep.kaf-profiles-sem.txt.overview.xls
		bus-accident.ont.dep.kaf-profiles-syn.txt.overview.xls
		bus-accident.ont.dep.kaf.sem.tpl
		bus-accident.ont.dep.kaf.sem.tpl.trp
		bus-accident.ont.dep.kaf.syn.tpl
		bus-accident.ont.dep.kaf.syn.tpl.trp
	profiles
		profiles-next.txt
		profiles-sem.txt
		profiles-syn.txt
	ontology
	    CommonSenseMapping.owl
	    DLP_397.owl
	    DOLCE-Lite.owl
	    export1.owl
	    ExtendedDnS.owl
	    FunctionalParticipation.owl
	    InformationObjects.owl
		Kyoto3Top.owl
		Kyoto3Middle.owl
		Kyoto3Domain.owl
		ModalDescriptions.owl
		Plans.owl
		SocialUnits.owl
		SpatialRelations.owl
		TemporalRelations.owl
	scripts
		run-profiles.sh
		convert-tuples-to-triples.sh
	lib
		vu.kafkybot.KafKybot-1.0-jar-with-dependencies.jar

LICENSE:
This work is licensed under a GNU GPL version 3.0: <http://fsf.org/>. See the file LICENSESOFTWARE.TXT and COPYING-GPL.TXT that should be in the
top-directory of this distribution.

DESCIPTION:
vu.kafkybot.KafKybot runs profiles over kaf files and extracts tuples with elements and relations. The profiles can be formulated using semantic and structural properties of the words in a text as represented in the KYOTO Annotation Format (KAF).

The program takes two obligatory arguments --kaf-file and --profiles as is shown in the script:

java -Xmx812m -cp ../lib/vu.kafkybot.KafKybot-1.0-jar-with-dependencies.jar vu.kafkybot.KafKybot
--kaf-file "../example/bus-accident.ont.dep.kaf" 
--profiles "../profiles/profiles-sem.txt" 
> "../example/bus-accident.ont.dep.kaf.sem.tpl" 

The kaf-file is the input kaf and --profiles refers to the file with the profiles.
The output is a send to the standard output stream. The output is in the form of tuples (see below) between:

- a sentence identifier
- any number of terms from the KAF file within that sentence that are related somehow

These tuples can be extracted on the basis of profiles that you can formulate. In these profiles, you specify the properties of a term to
match as an event or a participant and which other elements should occur to provide a match.

You can also run the program for a complete folder with files. In that case the path is to a folder or directory and an additional argument can be given to specify the file extension of the files to be considered, e.g.:

java -Xmx812m -cp ../lib/vu.kafkybot.KafKybot-1.0-jar-with-dependencies.jar vu.kafkybot.KafKybot
--kaf-file "../example" 
--profiles "../profiles/profiles-sem.txt" --extension ".kaf" > "../example/bus-accident.ont.dep.kaf.sem.tpl" 

In this example, all files in the folder example with the extension ".kaf" are processed with the same profiles and the result is stored in a single result output stream.

Finally, an optional argument --overview can be given. This will trigger the generation of an additional xls file with a simple overview of relations and quantification of the results.
Such an overview could look as follows:

participant:report:agent	2	event:kill:	1	event:say:	1		
participant:agency:agent	1	event:respond:	1				
participant:road:agent	3	event:use:	1	event:travel:	1	event:collide:	1
event:say:	1	participant:report:agent	1				
event:die:	1	participant:people:agent	1				
event:respond:	2	participant:agency:agent	1	participant:firefighter:agent	1		
event:collide:	2	participant:bus:agent	1	participant:road:agent	1		
participant:official:agent	1	event:confirm:	1				
participant:firefighter:agent	1	event:respond:	1				
participant:people:agent	2	event:kill:	1	event:die:	1		
event:confirm:	1	participant:official:agent	1				
event:use:	1	participant:road:agent	1				
event:travel:	1	participant:road:agent	1				
event:kill:	2	participant:people:agent	1	participant:report:agent	1		
participant:bus:agent	1	event:collide:	1				


PROFILE SYNTAX

A profile consists of a line with the profile name and optionally its confidence value (an integer value) followed by any number of lines 
that define the constraints for terms that should be matched within a sentence in the listed order.
The constraints have the syntax: "name of the layer":"name of the attribute"="value of the attribute":

For example:

profile=agent-1-sem;confidence=20
id=1;type=participant;role=agent;term:reference=*#material-artifact OR *#social-object OR *#agentive-physical-object;term:pos=N*
id=2;type=stop;term:pos=WRB*
id=3;type=event;term:reference=*#event;term:pos=V*;term:lemma=!have;term:lemma=!be

Here the attributes all apply to the term layer in KAF.

The profile is named "agent-1-sem", which has a confidence value of 20, has 3 variables (id=1,2,3) that will be matched against every term 
in every sentence in the KAF file. Each of the variables, specified on separate lines, has a number of feature-value (fv) pairs separated by ";". 
There are 3 fv-pairs that are used for administration purposes:

id:			holds a unique name for the variable
type:		any string can be used as a type which will be represented as a tuple element in the output, e.g.:
			<participant> -> this term is labeled as a participant result in the tuple
			<event> -> this term is labeled as an event result in the tuple
			
			Two special values are used that do not result in output:
			<stop> -> this pattern is not allowed to match anything beyond this term.
			        If specified in between two variables it implies that this term is not allowed to occur in between them.
			<skip> if the value of type is <skip> the terms are used for matching but not represented in the output

role:		any string can be used to fill the role attribute in the output

All other fv-pairs are used as constraints on the terms to be matched. Any of the attributes of the terms in KAF can be used as a contraint,
but also all the attributes of the embedded elements.
		
All fv pairs specified on a single line need to be present in a term to have match and also 
all the lines for a profile need to be matched on the terms of a sentence to have a match.

Multiple values can be listed using " OR " (spaces and capitals are required!). For example:

	term:reference=*#material-artifact OR *#social-object OR *#agentive-physical-object;

should be read as a disjunction of values for the term feature "reference".

It is also possible to exclude values by placing an "!" at the start  of the value. For example:

	term:lemma=!be

means that the terms should not have "be" as a lemma value.

Finally, it is possible to use "*" to indicate that values should end and/or start with a string. For example:

	term:reference=*#event*

means that "#event" should occur as a substring anywhere in the value.

The following examples of profiles illustrate the use of attributes at the dependency layer and at the chunk layer:

profile=dep-subj-obj
id=1;type=participant; role=p1;term:pos=N*;dep:rfunc=nsubj;dep:to=2;
id=2;type=event;term:pos=V*;term:lemma=!have;term:lemma=!be;
id=3;type=participant; role=p2;term:pos=N*;dep:rfunc=dobj;dep:to=2;

In the profile dep-subj-obj, two participants are defined related to an event represented by a verb. The first (id=1) matches a term that also occurs in a dependency relation with the value "nsubj" for the attribute "rfunc" and the value for the "to" attribute matches the term identifier of the second variable in the profile (id=2), which is represented by the verb. Similarly, the second participant (variable id=3) has a dependency to the same verb but the value for rfunc is "dobj". This reflects a standard transitive sentence where the subject comes first, followed by the main verb, followed by the direct object.

In the next example, a passive construction is captured, where the direct object is the syntactic subject occurring before the verb. In this case, we also added two moer constraints on the chunk layer of the first term:

profile=dep-obj-pass
id=1;type=participant;role=p2;term:pos=N*;dep:rfunc=nsubjpass;dep:to=2;chunk:phrase=NP;chunk:head;
id=2;type=event;term:pos=V*;term:lemma=!have;term:lemma=!be;chunk:phrase=V*;

The noun that occurs in the dependency relation to the verb also needs to be part of a NP constituent and it should be the head of that constituent. Note that the value for the head attribute can remain empty. If empty, it matches the identifier if the term that is being matched: the term itself should be the head. Currently, there is no option yet to indicate another variable to be the head. It is possible to exclude a term from being the head. In that case, you can put in the exclamation mark as a value, e.g. "chunk:head=!".

For semantic constraints, the reference attribute can be used that is part of the difference external references related to a term.
The reference attributes in the KAF file are derived from the KYOTO ontology. The KYOTO ontology is an extension of the DOLCE ontology 
and many (not all!!) synsets of Wordnet have been mapped to the ontology. 
More information on the ontology can be found on the KYOTO sensate: www.kyoto-project.eu.
The ontology is included in the resources folder and can be loaded with Protoge: http://protege.stanford.edu/

You can formulate as many profiles as needed in a profile file. Each of the profiles is matched separately against each sentence in KAF.
Different profiles can extract the same tuples or they can extract complementary tuple information. It may be necessary to post-process
the output to remove duplicates or merge complementary tuples. For example, you may wish to merge participants that are related to the same event
but are extracted with different profiles. 

OUTPUT SYNTAX:

The output tuples are grouped by a tuples element based on the file from which they are extracted. Each tuple has an unique id, 
the name of the profile that generated the output, the confidence score for that profile and the sentence identifier as attributes.
The elements of the tuple (which can be any number and you can give it any name) have attributes for the concept that lead to a match (if any), 
the confidence score of the concept (output of the word-sense-disambiguation program), the reference to the semantic type if there was any match, 
the lemma, the part-of-speech (pos) and possibly the role  (the relation value of the profile). Furthermore there is an  attribute for the mention in the KAF term layer and the range of tokens
in the KAF token layer (multiple tokens separated by ";"):

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kafkybot-results>
  <tuples source="bus-accident.ont.dep.kaf">
    <tuple id="1" profile="agent-1-sem" profileConfidence="30" sentenceId="s2">
      <!--Firefighters from multiple agencies responded to Highway 38 near Bryant Street in Mentone about 6:30 p.m. .-->
      <event concept="eng-30-00717358-v" confidence="0.703748" lemma="respond" mention="t33" pos="VBD" tokens="w34"/>
      <participant concept="eng-30-10091651-n" confidence="1.0" lemma="firefighter" mention="t29" pos="NNS" reference="ExtendedDnS.owl#social-object" role="agent" tokens="w30"/>
    </tuple>
    <tuple id="2" profile="agent-1-sem" profileConfidence="30" sentenceId="s2">
      <!--Firefighters from multiple agencies responded to Highway 38 near Bryant Street in Mentone about 6:30 p.m. .-->
      <event concept="eng-30-00717358-v" confidence="0.703748" lemma="respond" mention="t33" pos="VBD" tokens="w34"/>
      <participant concept="eng-30-08057206-n" confidence="0.00369784" lemma="agency" mention="t32" pos="NNS" reference="ExtendedDnS.owl#social-object" role="agent" tokens="w33"/>
    </tuple>
    <tuple id="3" profile="agent-1-sem" profileConfidence="30" sentenceId="s1">
      <!--Several people died and 27 people were injured on Sunday when a private charter tour bus coming down a mountain road collided with an SUV and another car .-->
      <event concept="eng-30-00358431-v" confidence="0.662059" lemma="die" mention="t3" pos="VBD" tokens="w3"/>
      <participant concept="eng-30-08160276-n" confidence="0.0567295" lemma="people" mention="t2" pos="NNS" reference="ExtendedDnS.owl#social-object" role="agent" tokens="w2"/>
    </tuple>
    <tuple id="4" profile="agent-1-sem" profileConfidence="30" sentenceId="s1">
      <!--Several people died and 27 people were injured on Sunday when a private charter tour bus coming down a mountain road collided with an SUV and another car .-->
      <event concept="eng-30-01561143-v" confidence="0.493632" lemma="collide" mention="t22" pos="VBD" tokens="w22"/>
      <participant concept="eng-30-02924116-n" confidence="0.682816" lemma="bus" mention="t16" pos="NN" reference="ExtendedDnS.owl#material-artifact" role="agent" tokens="w16"/>
    </tuple>
</tuples>

RUNNING THE SCRIPT
For running the scripts, you may need to adapt the paths to the sources. 

java -Xmx812m -cp ../lib/vu.kafkybot.KafKybot-1.0-jar-with-dependencies.jar vu.kafkybot.KafKybot --kaf-file "../example/bus-accident.ont.dep.kaf" --profiles "../profiles/profiles.txt" > "../example/bus-accident.ont.dep.kaf.trp"

The program takes a kaf-file and a file with the profiles as input. The result are written to the standard output as a stream. 

The resulting tuples can be converted to triples. This is a format defined for evaluating complex semantic structures. Any tuple can be converted
in as series of triples, where one element is the head of the tuple and the other are the children. The function will create a separate triple for each
pair of head and child element int he tuple. It will take the role attribute of the child for the relation in the triple. A tuple with 4 elements is thus converted
into 3 triples, where one is the head and the other four are the children. The following call is given in the example script "convert-tuples-to-triples.sh":

java -Xmx812m -cp ../lib/vu.kafkybot.KafKybot-1.0-jar-with-dependencies.jar vu.kafkybot.ConvertTuplesToTriples --tuple-file "../example/bus-accident.ont.dep.kaf.sem.tpl"  --first-element event

It will look for tuples in the input file where the parent element is named "event" and all other elements are its children. The role attribute of these children specifies
the relation for the triple. The above tuples are thus converted as follows:
		
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<triples>
<triple id="bus-accident.ont.dep.kaf#1" profile_id="agent-1-sem" profile_confidence="30" relation="agent">
	<elementFirstIds label="event" comment="respond">
		<elementFirst id="w34"/>
	</elementFirstIds>
	<elementSecondIds label="participant" comment="firefighter">
		<elementSecond id="w30"/>
	</elementSecondIds>
</triple>
<triple id="bus-accident.ont.dep.kaf#2" profile_id="agent-1-sem" profile_confidence="30" relation="agent">
	<elementFirstIds label="event" comment="respond">
		<elementFirst id="w34"/>
	</elementFirstIds>
	<elementSecondIds label="participant" comment="agency">
		<elementSecond id="w33"/>
	</elementSecondIds>
</triple>
<triple id="bus-accident.ont.dep.kaf#3" profile_id="agent-1-sem" profile_confidence="30" relation="agent">
	<elementFirstIds label="event" comment="die">
		<elementFirst id="w3"/>
	</elementFirstIds>
	<elementSecondIds label="participant" comment="people">
		<elementSecond id="w2"/>
	</elementSecondIds>
</triple>
<triple id="bus-accident.ont.dep.kaf#4" profile_id="agent-1-sem" profile_confidence="30" relation="agent">
	<elementFirstIds label="event" comment="collide">
		<elementFirst id="w22"/>
	</elementFirstIds>
	<elementSecondIds label="participant" comment="bus">
		<elementSecond id="w16"/>
	</elementSecondIds>
</triple>
<triples>

vu.kafkybot.KafKybot is compiled on Mac OS X version 10.6.8 with Java 1.6. The source code depends on the KafSaxParser that was
developed in the KYOTO project: www.kyoto-project.eu
