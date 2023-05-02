import polars as pl

data = pl.read_parquet('/Users/manolocabello/Downloads/CalidadDelAire_UfId_DispositivoId_H_2454_504.parquet')        
print('leyo ok', data)
