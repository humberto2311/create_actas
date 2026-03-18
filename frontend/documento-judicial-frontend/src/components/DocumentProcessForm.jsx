import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
  FormControlLabel,
  Switch,
  MenuItem,
  Box,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  InputAdornment,
  Tab,
  Tabs,
  Chip
} from '@mui/material';
import {
  Save as SaveIcon,
  Refresh as RefreshIcon,
  Search as SearchIcon,
  Clear as ClearIcon,
  Add as AddIcon
} from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { ToastContainer } from 'react-toastify';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';
import 'react-toastify/dist/ReactToastify.css';

import { documentProcessSchema, audienceTypes, nacionOptions } from '../utils/validation';
import DocumentCard from './DocumentCard';
import {
  useDocuments,
  useCreateDocument,
  useUpdateDocument,
  useDeleteDocument,
  useGenerateZip
} from '../hooks/useDocumentQueries';

const DocumentProcessForm = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedId, setSelectedId] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedDocument, setSelectedDocument] = useState(null);
  const [tabValue, setTabValue] = useState(0);

  // React Query hooks
  const { data: documents = [], isLoading, refetch } = useDocuments();
  const createMutation = useCreateDocument();
  const updateMutation = useUpdateDocument();
  const deleteMutation = useDeleteDocument();
  const generateMutation = useGenerateZip();

  const {
    control,
    handleSubmit,
    reset,
    setValue,
    formState: { errors }
  } = useForm({
    resolver: yupResolver(documentProcessSchema),
    defaultValues: {
      names: '',
      lastNames: '',
      identity: '',
      nacion: 'COLOMBIANA',
      date: new Date().toISOString().split('T')[0],
      captura: new Date().toISOString().split('T')[0],
      conduct: '',
      radicado: '',
      fiscal: '',
      typeAudience: '',
      fact: '',
      juzgado: '',
      state: true
    }
  });

  const onSubmit = async (data) => {
    if (selectedId) {
      await updateMutation.mutateAsync({ id: selectedId, data });
    } else {
      await createMutation.mutateAsync(data);
    }
    
    reset();
    setSelectedId(null);
    setTabValue(0);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este documento?')) {
      await deleteMutation.mutateAsync(id);
      
      if (selectedId === id) {
        reset();
        setSelectedId(null);
      }
    }
  };

  const handleEdit = (doc) => {
    setSelectedId(doc.id);
    reset(doc);
    setTabValue(0);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleView = (doc) => {
    setSelectedDocument(doc);
    setOpenDialog(true);
  };

  const handleGenerateDocument = async (id) => {
    await generateMutation.mutateAsync(id);
  };

  const handleNewDocument = () => {
    reset();
    setSelectedId(null);
    setTabValue(0);
  };

  // Filtrado
  const filteredDocuments = documents.filter(doc => 
    doc.names?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doc.lastNames?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doc.identity?.includes(searchTerm)
  );

  const activeDocuments = filteredDocuments.filter(doc => doc.state);
  const inactiveDocuments = filteredDocuments.filter(doc => !doc.state);

  const getDocumentsToShow = () => {
    switch (tabValue) {
      case 1: return activeDocuments;
      case 2: return inactiveDocuments;
      default: return filteredDocuments;
    }
  };

  const documentsToShow = getDocumentsToShow();

  // Estado de carga combinado
  const isMutating = createMutation.isPending || updateMutation.isPending || deleteMutation.isPending;

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <ToastContainer position="top-right" autoClose={3000} />
      
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" component="h1">
          Gestión de Procesos Judiciales
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleNewDocument}
        >
          Nuevo Documento
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* Formulario */}
        <Grid size={{ xs: 12, md: 5 }}>
          <Paper elevation={3} sx={{ p: 3, borderRadius: 2, position: 'sticky', top: 20 }}>
            <Typography variant="h6" gutterBottom sx={{ mb: 3 }}>
              {selectedId ? 'Editar Documento' : 'Nuevo Documento'}
            </Typography>

            <form onSubmit={handleSubmit(onSubmit)}>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="names"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Nombres *"
                        fullWidth
                        error={!!errors.names}
                        helperText={errors.names?.message}
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="lastNames"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Apellidos *"
                        fullWidth
                        error={!!errors.lastNames}
                        helperText={errors.lastNames?.message}
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="identity"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Identificación *"
                        fullWidth
                        error={!!errors.identity}
                        helperText={errors.identity?.message}
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="nacion"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        select
                        label="Nacionalidad *"
                        fullWidth
                        error={!!errors.nacion}
                        helperText={errors.nacion?.message}
                        size="small"
                      >
                        {nacionOptions.map((option) => (
                          <MenuItem key={option.value} value={option.value}>
                            {option.label}
                          </MenuItem>
                        ))}
                      </TextField>
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="date"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Fecha del Proceso *"
                        type="date"
                        fullWidth
                        error={!!errors.date}
                        helperText={errors.date?.message}
                        size="small"
                        InputLabelProps={{ shrink: true }}
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="captura"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Fecha de Captura *"
                        type="date"
                        fullWidth
                        error={!!errors.captura}
                        helperText={errors.captura?.message}
                        size="small"
                        InputLabelProps={{ shrink: true }}
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="radicado"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Radicado"
                        fullWidth
                        error={!!errors.radicado}
                        helperText={errors.radicado?.message}
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="fiscal"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Fiscal"
                        fullWidth
                        error={!!errors.fiscal}
                        helperText={errors.fiscal?.message}
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="juzgado"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Juzgado"
                        fullWidth
                        error={!!errors.juzgado}
                        helperText={errors.juzgado?.message}
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Controller
                    name="conduct"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Delito/Conducta"
                        fullWidth
                        error={!!errors.conduct}
                        helperText={errors.conduct?.message}
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Controller
                    name="fact"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Descripción de los Hechos"
                        fullWidth
                        multiline
                        rows={3}
                        error={!!errors.fact}
                        helperText={errors.fact?.message}
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Controller
                    name="state"
                    control={control}
                    render={({ field: { value, onChange } }) => (
                      <FormControlLabel
                        control={
                          <Switch
                            checked={value}
                            onChange={onChange}
                          />
                        }
                        label={value ? "Activo" : "Inactivo"}
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                    {selectedId && (
                      <Button variant="outlined" onClick={handleNewDocument}>
                        Cancelar
                      </Button>
                    )}
                    <Button
                      type="submit"
                      variant="contained"
                      startIcon={<SaveIcon />}
                      disabled={isMutating}
                    >
                      {isMutating ? <CircularProgress size={24} /> : (selectedId ? 'Actualizar' : 'Guardar')}
                    </Button>
                  </Box>
                </Grid>
              </Grid>
            </form>
          </Paper>
        </Grid>

        {/* Lista de Documentos */}
        <Grid size={{ xs: 12, md: 7 }}>
          <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
            <Box sx={{ mb: 3 }}>
              <TextField
                fullWidth
                size="small"
                placeholder="Buscar..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon />
                    </InputAdornment>
                  ),
                  endAdornment: searchTerm && (
                    <InputAdornment position="end">
                      <IconButton size="small" onClick={() => setSearchTerm('')}>
                        <ClearIcon />
                      </IconButton>
                    </InputAdornment>
                  )
                }}
              />
            </Box>

            <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} sx={{ mb: 2 }}>
              <Tab label={`Todos (${filteredDocuments.length})`} />
              <Tab label={`Activos (${activeDocuments.length})`} />
              <Tab label={`Inactivos (${inactiveDocuments.length})`} />
            </Tabs>

            <Box sx={{ maxHeight: 600, overflow: 'auto' }}>
              {isLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <>
                  {documentsToShow.map((doc) => (
                    <DocumentCard
                      key={doc.id}
                      document={doc}
                      onEdit={handleEdit}
                      onDelete={handleDelete}
                      onGenerate={handleGenerateDocument}
                      onView={handleView}
                      isDeleting={deleteMutation.isPending}
                    />
                  ))}
                  
                  {documentsToShow.length === 0 && (
                    <Alert severity="info">No hay documentos para mostrar</Alert>
                  )}
                </>
              )}
            </Box>

            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
              <Button 
                startIcon={<RefreshIcon />} 
                onClick={() => refetch()} 
                size="small"
                disabled={isLoading}
              >
                Actualizar
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      {/* Diálogo de detalles */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Detalles del Documento</DialogTitle>
        <DialogContent dividers>
          {selectedDocument && (
            <Grid container spacing={2}>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2">Nombres</Typography>
                <Typography gutterBottom>{selectedDocument.names} {selectedDocument.lastNames}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2">Identificación</Typography>
                <Typography gutterBottom>{selectedDocument.identity}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2">Nacionalidad</Typography>
                <Typography gutterBottom>
                  {nacionOptions.find(n => n.value === selectedDocument.nacion)?.label}
                </Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2">Fecha del Proceso</Typography>
                <Typography gutterBottom>
                  {selectedDocument.date && format(new Date(selectedDocument.date), 'PPP', { locale: es })}
                </Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2">Fecha de Captura</Typography>
                <Typography gutterBottom>
                  {selectedDocument.captura && format(new Date(selectedDocument.captura), 'PPP', { locale: es })}
                </Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2">Estado</Typography>
                <Chip 
                  label={selectedDocument.state ? 'Activo' : 'Inactivo'}
                  color={selectedDocument.state ? 'success' : 'error'}
                  size="small"
                />
              </Grid>
              {selectedDocument.radicado && (
                <Grid size={{ xs: 6 }}>
                  <Typography variant="subtitle2">Radicado</Typography>
                  <Typography gutterBottom>{selectedDocument.radicado}</Typography>
                </Grid>
              )}
              {selectedDocument.fiscal && (
                <Grid size={{ xs: 6 }}>
                  <Typography variant="subtitle2">Fiscal</Typography>
                  <Typography gutterBottom>{selectedDocument.fiscal}</Typography>
                </Grid>
              )}
              {selectedDocument.conduct && (
                <Grid size={{ xs: 12 }}>
                  <Typography variant="subtitle2">Conducta</Typography>
                  <Typography gutterBottom>{selectedDocument.conduct}</Typography>
                </Grid>
              )}
              {selectedDocument.fact && (
                <Grid size={{ xs: 12 }}>
                  <Typography variant="subtitle2">Hechos</Typography>
                  <Typography gutterBottom>{selectedDocument.fact}</Typography>
                </Grid>
              )}
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cerrar</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default DocumentProcessForm;