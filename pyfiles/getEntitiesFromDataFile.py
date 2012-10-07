import os

data_folder = '\\\\162.105.71.12\\raw data\\'
entities = set()
nn = 0
for line in file(data_folder + 'dblp.rdf'):
    tks = line.strip().split('\t')
    entities.add(tks[0])
    if not tks[2].startswith('@'):
        entities.add(tks[2])
    nn += 1
    if nn % 100000 == 0 :
        print nn, len(entities)
print len(entities)
sw = open('dblp_entities.txt', 'w')
for entity in entities:
    sw.write(entity + '\n')
sw.close()
