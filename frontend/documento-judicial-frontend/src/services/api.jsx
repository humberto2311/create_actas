import axios from 'axios';

const API_URL = 'http://localhost:8080/api/document-process';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const documentService = {
  // Obtener todos los documentos
  getAll: async () => {
    const response = await api.get('/');
    return response.data;
  },

  // Obtener un documento por ID
  getById: async (id) => {
    const response = await api.get(`/${id}`);
    return response.data;
  },

  // Crear nuevo documento
  create: async (data) => {
    const response = await api.post('/', data);
    return response.data;
  },

  // Actualizar documento (asumiendo que existe endpoint PUT)
  update: async (id, data) => {
    const response = await api.put(`/${id}`, data);
    return response.data;
  },

  // Eliminar documento
  delete: async (id) => {
    await api.delete(`/${id}`);
  },

  // Generar documento Word
  generateDocument: async (id) => {
    const response = await api.get(`/${id}/generate-solicitud`, {
      responseType: 'blob'
    });
    return response.data;
  }
};