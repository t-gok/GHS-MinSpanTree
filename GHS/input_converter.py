import sys
import os


f = open(sys.argv[1])
lines = f.readlines()
f.close()


ADJ = []
no_of_nodes = 0


for line in lines:
	if(line.strip() == ""):
		continue

	tokens = line.strip().split(":")
	nodes = tokens[0].strip().split(",")
	n1 = int(nodes[0])
	n2 = int(nodes[1])
	wt = int(tokens[1])

	no_of_nodes = max(no_of_nodes,n1)
	no_of_nodes = max(no_of_nodes,n2)

	ADJ.append((n1,n2,wt))


no_of_nodes += 1

ADJ_M = []
for i in range(0,no_of_nodes):
	t = [0]*no_of_nodes
	ADJ_M.append(t)


for a in ADJ:
	(n1,n2,wt) = a
	ADJ_M[n1][n2] = wt
	ADJ_M[n2][n1] = wt

f = open('temp.txt','w')
for i in range(0,no_of_nodes):
	t = ADJ_M[i]
	for s in t:
		f.write(str(s)+" ")

	f.write("\n")

f.close()

