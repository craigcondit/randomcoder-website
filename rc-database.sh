#!/usr/bin/env bash

is_healthy() {
  docker exec -it --user postgres randomcoder-postgres psql -c 'select 1' >/dev/null 2>/dev/null
}

wait_for_healthy() {
  for n in `seq 1 30`; do
    if is_healthy; then
      return 0
    fi
    sleep 1
  done
  return 1
}

apply_script() {
  script="$1"
  echo "Applying script ${script}..."
  docker exec -i --user postgres randomcoder-postgres psql < "src/main/resources/org/randomcoder/website/database/${script}.sql"
}

do_start() {
  echo "Starting database..."
  docker run --rm=true -d \
    --name randomcoder-postgres \
    -e POSTGRES_PASSWORD=postgres \
    -p 5432:5432 \
    library/postgres:15-alpine

  echo "Waiting for database to become healthy..."
  if ! wait_for_healthy; then
    echo "Failed to become healthy, aborting."
  fi

  echo "Setting up database..."
  for script in \
      create \
      populate \
      upgrade-1.1 \
      upgrade-1.2 \
      upgrade-1.3 \
      upgrade-2.0 \
      upgrade-2.2 \
      upgrade-2.6 \
      upgrade-3.0 \
      upgrade-4.0 \
      test ; do
    apply_script "${script}"
  done
}

do_stop() {
  echo "Stopping database..."
  docker rm -f randomcoder-postgres >/dev/null 2>/dev/null || :
}

do_psql() {
  exec docker exec -it --user=postgres randomcoder-postgres psql
}

cmd=$1

case $cmd in 
  start)
    do_start
    ;;
  stop)
    do_stop
    ;;
  psql)
    do_psql
    ;;
   *)
    echo "Usage: $0 [start|stop|psql]" >&2
    exit 1
    ;;
esac

