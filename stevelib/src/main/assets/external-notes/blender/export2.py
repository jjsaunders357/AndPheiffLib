import bpy

basePath = "C:\\Users\\Steve\\AndroidStudioProjects\\AndPheiffLib\\stevelib\\src\\main\\assets\\meshes\\"


def writeVector(file, vector):
    file.write(str(vector.x) + "," + str(vector.y) + "," + str(vector.z))


def writeColor(file, color):
    file.write(str(color.r) + "," + str(color.g) + "," + str(color.b))


def writeFace(file, face):
    if len(face.vertices) == 3:
        file.write(str(face.vertices[0]) + ",")
        file.write(str(face.vertices[1]) + ",")
        file.write(str(face.vertices[2]))
    else:
        file.write(str(face.vertices[0]) + ",")
        file.write(str(face.vertices[1]) + ",")
        file.write(str(face.vertices[2]) + ",")
        file.write(str(face.vertices[2]) + ",")
        file.write(str(face.vertices[3]) + ",")
        file.write(str(face.vertices[0]))


def cacheMaterial(material):
    if not material.name in materialMap:
        materialMap[material.name] = material


def exportMaterial(file, material):
    file.write("MATERIAL_VNI\n")
    file.write(material.name + "\n")
    if material.active_texture == None or material.active_texture.image == None:
        textureFileName = "NoTexture"
    else:
        textureFileName = material.active_texture.image.name
    file.write(textureFileName + "\n")
    file.write(str(material.ambient) + "\n")
    file.write(str(material.translucency) + "\n")
    writeColor(file, material.diffuse_color)
    file.write("\n")
    writeColor(file, material.specular_color)
    file.write("\n")
    return textureFileName


def exportMesh(file, object):
    file.write("MESH_VNI\n")
    mesh = object.to_mesh(bpy.context.scene, True, "RENDER")
    mesh.update(calc_tessface=True)
    if len(mesh.materials) == 0:
        file.write("NoMaterial\n")
    else:
        file.write(mesh.materials[0].name + "\n")
        cacheMaterial(mesh.materials[0])

    file.write(object.name + "\n")
    file.write(str(len(mesh.vertices)) + "\n")
    for i in range(0, len(mesh.vertices) - 1):
        writeVector(file, mesh.vertices[i].co)
        file.write(",")
    writeVector(file, mesh.vertices[-1].co)
    file.write("\n")
    for i in range(0, len(mesh.vertices) - 1):
        writeVector(file, mesh.vertices[i].normal)
        file.write(",")
    writeVector(file, mesh.vertices[-1].normal)
    file.write("\n")
    numIndices = 0
    for i in range(0, len(mesh.tessfaces)):
        face = mesh.tessfaces[i]
        if len(face.vertices) == 3:
            numIndices += 3
        else:
            numIndices += 6
    file.write(str(numIndices) + "\n")
    for i in range(0, len(mesh.tessfaces) - 1):
        writeFace(file, mesh.tessfaces[i])
        file.write(",")
    writeFace(file, mesh.tessfaces[-1])


def exportMeshes(fileName):
    file = open(basePath + fileName, "w")
    try:
        for i in range(0, len(bpy.data.objects)):
            object = bpy.data.objects[i]
            if object.type == "MESH":
                exportMesh(file, object)
                file.write("\n")
        for material in materialMap.values():
            exportMaterial(file, material)
    finally:
        file.close()


materialMap = {}
exportMeshes("monkey.mesh")
