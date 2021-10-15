#!/bin/bash
build_dir="./tmp_build"
output_dir='./plugins/paint'
src_dir="./src"
jarname="DrawEngineCoolAdapterFactory.jar"
interface_provided="fr.uge.poo.paint.ex8.engine.DrawEngineFactory"
class_providing_the_interface="fr.uge.poo.paint.ex8.engine.DrawEngineCoolAdapterFactory"
included_classes=("fr.uge.poo.paint.ex8.engine.DrawEngineCoolAdapter" "com.evilcorp.coolgraphics.CoolGraphics" "$interface_provided" "$class_providing_the_interface")

#Option to create a jar compatible with older versions of java
#compile_options="--release 11"
compile_options=""

# check that the build directory does not exists

if [ -d "$build_dir" ]; then
	echo "The temporary build folder $build_dir already exists. Please remove it before proceeding."
	exit 1
else
	echo "Creating the temporary build folder $build_dir"
	mkdir "$build_dir"
fi

# check that the source directory is present

if [ ! -d "./src" ]; then
	echo "The source directory file $src_dir does not exists. Please make sure that you are running the script at the root of your workspace."
	exit 1
fi

for classe in ${included_classes[@]}; do
	classe_src="$src_dir/${classe//.//}.java"
	echo "Compiling $classe_src"
	javac $compile_options -cp "$src_dir" -d "$build_dir" "./$classe_src"
done

# creation of the services directory and file
mkdir -p "$build_dir/META-INF/services"
echo "$class_providing_the_interface" > "$build_dir/META-INF/services/$interface_provided"
jar cvf "$output_dir/$jarname" -C "$build_dir/" .

# safe delete build directory
find "$build_dir/" -name "*.class" -type f -delete
rm "$build_dir/META-INF/services/$interface_provided" 
find "$build_dir/" -mindepth 1 -type d -empty -delete
rmdir "$build_dir"
