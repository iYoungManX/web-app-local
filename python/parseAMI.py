# -*- coding: utf-8 -*-



with open('packer', 'r') as f:
    lines = f.readlines()

    # Iterate through the lines in reverse order
    for line in reversed(lines):
        # Check if the line is not empty
        if line.strip():
            last_line = line.strip()
            break

res= last_line.split(" ")[1]
print("TF_VAR_ami_id=" + str(res))



