
DIR="${1:-src/test/resources/sql}"


echo "Generating @SQL Annotation. Put into test/java/.../OnyxTest"

echo '@Sql({'

find "$DIR" -maxdepth 1 -type f -name "*.sql" | sort | while read -r file; do
  filename="$(basename "$file")"
  echo "    \"classpath:sql/${filename}\","
done

echo '})'