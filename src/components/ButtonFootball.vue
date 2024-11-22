<script>
import { ref, onMounted } from 'vue';
import { TabulatorFull as Tabulator } from 'tabulator-tables'
import 'tabulator-tables/dist/css/tabulator_bootstrap5.min.css'
import axios from 'axios'

export default {
  setup() {
    const table = ref(null);
    const items = ref([]);

    onMounted(async () => {
      try {
        const response = await axios.get('http://localhost:8080/teams?name=Corinthians')
        items.value = await response.data
      } catch (error) {
        console.error('Error fetching API data: ', error)
      }
      
      new Tabulator(table.value, {
        data: [ items.value ],
        layout: "fitColumns",
        columns: [
          { title: "Nome", field: "name" },
          { title: "Tipo", field: "type" },
          { title: "Nome Completo", field: "fullName" },
          { title: "Fundação", field: "foundation" },
          { title: "Cidade", field: "city" },
          { title: "País", field: "country" },
          { title: "Escudo", field: "logoImgFile" },
        ],
      });
    });

    return { table };
  },
};
</script>

<template>
  <div ref="table"></div>
</template>