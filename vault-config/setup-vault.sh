set -e
export VAULT_ADDR='http://vault:8200'
export VAULT_TOKEN='my-root-token'

until vault status > /dev/null 2>&1; do
  echo "Ожидание запуска Vault..."
  sleep 2
done

if vault kv get secret/car-booking-app > /dev/null 2>&1; then
  echo "Секрет JWT уже существует в Vault. Ничего не делаем."
else
  echo "Секрет JWT не найден. Генерируем новый..."
  NEW_JWT_SECRET=$(head -c 32 /dev/urandom | od -A n -t x1 | tr -d ' \n')

  vault kv put secret/car-booking-app JWT_SECRET="$NEW_JWT_SECRET"

  echo "Новый секрет JWT успешно сгенерирован и сохранен в Vault."
fi

echo "Настройка Vault завершена."