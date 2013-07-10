<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kafkybot-results>
  <tuples source="bus-accident.ont.dep.kaf">
    <tuple id="1" profile="dep-subj" profileConfidence="40" sentenceId="s2">
      <!--Firefighters from multiple agencies responded to Highway 38 near Bryant Street in Mentone about 6:30 p.m. .-->
      <event lemma="respond" mention="t33" pos="VBD" tokens="w34"/>
      <participant dep="nsubj" lemma="firefighter" mention="t29" pos="NNS" role="p1" tokens="w30"/>
    </tuple>
    <tuple id="2" profile="dep-subj" profileConfidence="40" sentenceId="s1">
      <!--Several people died and 27 people were injured on Sunday when a private charter tour bus coming down a mountain road collided with an SUV and another car .-->
      <event lemma="die" mention="t3" pos="VBD" tokens="w3"/>
      <participant dep="nsubj" lemma="people" mention="t2" pos="NNS" role="p1" tokens="w2"/>
    </tuple>
    <tuple id="3" profile="dep-subj" profileConfidence="40" sentenceId="s1">
      <!--Several people died and 27 people were injured on Sunday when a private charter tour bus coming down a mountain road collided with an SUV and another car .-->
      <event lemma="collide" mention="t22" pos="VBD" tokens="w22"/>
      <participant dep="nsubj" lemma="bus" mention="t16" pos="NN" role="p1" tokens="w16"/>
    </tuple>
    <tuple id="4" profile="dep-obj" profileConfidence="30" sentenceId="s1">
      <!--Several people died and 27 people were injured on Sunday when a private charter tour bus coming down a mountain road collided with an SUV and another car .-->
      <participant dep="dobj" lemma="road" mention="t21" pos="NN" role="p2" tokens="w21"/>
      <event lemma="come" mention="t17" pos="VBG" tokens="w17"/>
    </tuple>
    <tuple id="5" profile="dep-obj-pass" profileConfidence="20" sentenceId="s1">
      <!--Several people died and 27 people were injured on Sunday when a private charter tour bus coming down a mountain road collided with an SUV and another car .-->
      <event lemma="injure" mention="t8" pos="VBN" tokens="w8"/>
      <participant dep="nsubjpass" lemma="people" mention="t6" pos="NNS" role="p2" tokens="w6"/>
    </tuple>
    <tuple id="6" profile="dep-obj-pass" profileConfidence="20" sentenceId="s7">
      <!--Four other people in two other vehicles were involved in the crash .-->
      <event lemma="involve" mention="t105" pos="VBN" tokens="w117"/>
      <participant dep="nsubjpass" lemma="people" mention="t99" pos="NNS" role="p2" tokens="w111"/>
    </tuple>
    <tuple id="7" profile="dep-obj" profileConfidence="30" sentenceId="s5">
      <!--But officials had yet to confirm the number .-->
      <participant dep="dobj" lemma="number" mention="t89" pos="NN" role="p2" tokens="w99"/>
      <event lemma="confirm" mention="t87" pos="VB" tokens="w97"/>
    </tuple>
    <tuple id="8" profile="dep-obj-pass" profileConfidence="20" sentenceId="s3">
      <!--The road is used to travel to and from the mountain resort of Big Bear , about 90 miles east of Los Angeles .-->
      <event lemma="use" mention="t45" pos="VBN" tokens="w50"/>
      <participant dep="nsubjpass" lemma="road" mention="t43" pos="NN" role="p2" tokens="w48"/>
    </tuple>
    <tuple id="9" profile="dep-subj" profileConfidence="40" sentenceId="s4">
      <!--Initial reports said that as many as 10 people may have been killed when they were ejected from a bus .-->
      <event lemma="say" mention="t64" pos="VBD" tokens="w73"/>
      <participant dep="nsubj" lemma="report" mention="t63" pos="NNS" role="p1" tokens="w72"/>
    </tuple>
    <tuple id="10" profile="dep-obj-pass" profileConfidence="20" sentenceId="s4">
      <!--Initial reports said that as many as 10 people may have been killed when they were ejected from a bus .-->
      <event lemma="kill" mention="t74" pos="VBN" tokens="w83"/>
      <participant dep="nsubjpass" lemma="people" mention="t70" pos="NNS" role="p2" tokens="w79"/>
    </tuple>
  </tuples>
</kafkybot-results>
