if [ -d jni ]; then
	rm -rf jni;
fi

mkdir jni
mkdir jni/android;

cp -r ../../NCL/Source/android jni/.
cp -r ../../NCL/android/compiled_sdl  jni/.
cp -r ../../libraries jni/.
cp -r ../../../nsp jni/.
cp -r ../../../security jni/.

cp -r ../../NCL/Source/android/*.[chm] jni/android/.
cp -r ../../NCL/android/*.mk  jni/.
cp -r ../../NCL/Source/*.[chm] jni/.
cp Android.mk jni/.
