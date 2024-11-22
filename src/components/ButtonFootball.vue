<script>
import { ref, onMounted } from 'vue';
import { TabulatorFull as Tabulator } from 'tabulator-tables';
// import 'tabulator-tables/dist/css/tabulator.min.css';
import 'tabulator-tables/dist/css/tabulator_bootstrap5.min.css';

export default {
  setup() {
    const table = ref(null);

    onMounted(() => {
      new Tabulator(table.value, {
        data: [
          { name: "Oli Bob", age: "12" },
          { name: "Mary May", age: "1" },
          { name: "Christine Lobowski", age: "42" },
          { name: "Brendon Philips", age: "125" },
          { name: "Margret Marmajuke", age: "16" },
        ], // Your data here
        layout: "fitColumns",
        columns: [
          { title: "Name", field: "name" },
          { title: "Age", field: "age" },
        ],
      });
    });

    return {
      table,
    };
  },
};
</script>

<template>
  <div ref="table"></div>
</template>