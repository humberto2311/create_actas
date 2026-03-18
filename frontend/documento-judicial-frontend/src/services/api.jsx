import axios from 'axios';

const API_URL = 'http://localhost:8080/api/document-process';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json' // Asegura que el backend reciba JSON
  }
});

export const documentService = {
  // Obtener todos los documentos
  getAll: async () => {
    const response = await api.get('');
    return response.data;
  },

  // Obtener un documento por ID
  getById: async (id) => {
    const response = await api.get(`/${id}`);
    return response.data;
  },

 // En api.jsx
create: async (data) => {
  try {
    console.log('Enviando datos:', data); // Para debug
    const response = await api.post('', data);
    return response.data;
  } catch (error) {
    console.error('Error completo:', error.response?.data); // Ver el error del backend
    throw error;
  }
},

  // Actualizar documento (asumiendo que existe endpoint PUT)
  update: async (id, data) => {
    const response = await api.put(`/${id}`, data);
    return response.data;
  },

  // Eliminar documento
delete: async (id) => {
  // Se agrega el prefijo /delete antes del ID
  await api.delete(`/delete/${id}`);
},

  
  // Generar ZIP con ambos documentos
  generateZip: async (id) => {
    try {
      const response = await api.get(`/${id}/generate-solicitud`, {
        responseType: 'blob'
      });
      
      // Obtener el nombre del archivo del header Content-Disposition si está disponible
      const contentDisposition = response.headers['content-disposition'];
      let filename = `documentos_${id}.zip`;
      
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
        if (filenameMatch && filenameMatch[1]) {
          filename = filenameMatch[1].replace(/['"]/g, '');
        }
      }
      
      return {
        blob: response.data,
        filename: filename
      };
    } catch (error) {
      console.error('Error al generar ZIP:', error);
      throw error;
    }
  }

};