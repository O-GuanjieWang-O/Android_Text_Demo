myArray=("./app/.gitignore",
"./.gitignore",
"./.idea/libraries/Gradle__com_github_bumptech_glide_annotations_4_3_1.xml",
"./.idea/libraries/Gradle__com_github_bumptech_glide_disklrucache_4_3_1.xml",
"./.idea/libraries/Gradle__com_github_dailystudio_devbricks_1_1_6_aar.xml",
"./.idea/libraries/Gradle__com_github_AdnanMahida_ZoomImageViewLibrary_1_0_0_aar.xml",
"./.idea/libraries/Gradle__com_github_bumptech_glide_gifdecoder_4_3_1.xml",
"./.idea/libraries/Gradle__com_github_bumptech_glide_glide_4_3_1.xml",
"./.idea/.gitignore"
)
for f in "${myArray[@]}";
do 
    echo "$f"
    rm -rf "$f";
 done
